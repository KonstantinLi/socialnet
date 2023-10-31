package ru.skillbox.socialnet;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import ru.skillbox.socialnet.dto.request.UserRq;
import ru.skillbox.socialnet.entity.personrelated.Person;
import ru.skillbox.socialnet.repository.PersonRepository;
import ru.skillbox.socialnet.security.JwtTokenUtils;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ContextConfiguration(initializers = {UserControllerTest.Initializer.class})
@Testcontainers
@Sql(value = {"/UserController-before-test.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(value = {"/UserController-after-test.sql"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
class UserControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PersonRepository personRepository;

    @Autowired
    private JwtTokenUtils jwtTokenUtils;

    @Container
    public static PostgreSQLContainer<?> postgreSqlContainer = new PostgreSQLContainer<>("postgres:15.1")
            .withDatabaseName("socialNet-test")
            .withUsername("root")
            .withPassword("root");

    static class Initializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {
        public void initialize(ConfigurableApplicationContext configurableApplicationContext) {
            TestPropertyValues.of(
                    "spring.datasource.url=" + postgreSqlContainer.getJdbcUrl(),
                    "spring.datasource.username=" + postgreSqlContainer.getUsername(),
                    "spring.datasource.password=" + postgreSqlContainer.getPassword(),
                    "spring.liquibase.enabled=true"
            ).applyTo(configurableApplicationContext.getEnvironment());
        }
    }

    @Test
    void getUserByIdTest() throws Exception {
        String token = getToken(1L);

        this.mockMvc.perform(get("/api/v1/users/1").header("Authorization", token))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.first_name").value("Prent"))
                .andExpect(jsonPath("$.data.last_name").value("Jendrys"));
    }

    @Test
    void getMyInfoTest() throws Exception {
        String token = getToken(2L);

        this.mockMvc.perform(get("/api/v1/users/me").header("Authorization", token))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.first_name").value("Jonie"))
                .andExpect(jsonPath("$.data.last_name").value("Twallin"));
    }

    @Test
    void getBlockedUserTest() throws Exception {
        this.mockMvc.perform(get("/api/v1/users/6").header("Authorization", getToken(1L)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("PersonIsBlockedException"))
                .andExpect(jsonPath("$.error_description").value("Пользователь заблокирован"));
    }

    @Test
    void getDeletedUserTest() throws Exception {
        this.mockMvc.perform(get("/api/v1/users/7").header("Authorization", getToken(1L)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("PersonNotFoundException"))
                .andExpect(jsonPath("$.error_description").value("Пользователь удален"));
    }

    @Test
    void getNonExistentUserTest() throws Exception {
        this.mockMvc.perform(get("/api/v1/users/11").header("Authorization", getToken(1L)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("PersonNotFoundException"))
                .andExpect(jsonPath("$.error_description").value("Пользователь с указанным id не найден"));
    }

    @Test
    void updateMyInfoTest() throws Exception {
        String token = getToken(3L);

        UserRq userRq = new UserRq();
        userRq.setAbout("Test about");
        userRq.setCity("Tokio");
        userRq.setCountry("Japan");
        userRq.setPhone("+81-88-8888-8888");
        userRq.setFirstName("Elijah");
        userRq.setLastName("Wood");
        userRq.setMessagesPermission("ALL");
        userRq.setBirthDate("1998-03-19T00:00");

        ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
        String requestBody = ow.writeValueAsString(userRq);

        this.mockMvc.perform(put("/api/v1/users/me")
                    .header("Authorization", token)
                    .content(requestBody)
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.city").value("Tokio"))
                .andExpect(jsonPath("$.data.country").value("Japan"))
                .andExpect(jsonPath("$.data.last_name").value("Wood"))
                .andExpect(jsonPath("$.data.first_name").value("Elijah"))
                .andExpect(jsonPath("$.data.phone").value("+81-88-8888-8888"))
                .andExpect(jsonPath("$.data.messages_permission").value("ALL"))
                .andExpect(jsonPath("$.data.birth_date").value("1998-03-19T00:00:00"));
    }

    @Test
    void deleteMyInfoTest() throws Exception {
        String token = getToken(4L);

        this.mockMvc.perform(delete("/api/v1/users/me").header("Authorization", token))
                .andDo(print())
                .andExpect(status().isOk());

        Optional<Person> personOptional = personRepository.findById(4L);
        assertAll(
                "Person is softly deleted",
                () -> assertTrue(personOptional.isPresent()),
                () -> assertTrue(personOptional.get().getIsDeleted())
        );
    }

    @Test
    void recoverUserInfoTest() throws Exception {
        String token = getToken(4L);

        this.mockMvc.perform(delete("/api/v1/users/me").header("Authorization", token))
                .andDo(print());

        this.mockMvc.perform(post("/api/v1/users/me/recover").header("Authorization", token))
                .andDo(print())
                .andExpect(status().isOk());

        Optional<Person> personOptional = personRepository.findById(4L);
        assertAll(
                "Person is recovered",
                () -> assertTrue(personOptional.isPresent()),
                () -> assertFalse(personOptional.get().getIsDeleted())
        );
    }

    @Test
    void findUsersTest() throws Exception {
        String token = getToken(5L);

        this.mockMvc.perform(get("/api/v1/users/search")
                    .header("Authorization", token)
                    .param("country", "United States")
                    .param("offset", "1")
                        .param("perPage", "2"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", Matchers.hasSize(2)))
                .andExpect(jsonPath("$.data[0].city").value("New York"))
                .andExpect(jsonPath("$.data[1].city").value("Los Angeles"));
    }

    private String getToken(Long id) {
        Person person = personRepository.findByIdImpl(id);
        return jwtTokenUtils.generateToken(person);
    }
}
