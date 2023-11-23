package ru.skillbox.socialnet;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import ru.skillbox.socialnet.dto.response.CommonRs;
import ru.skillbox.socialnet.entity.personrelated.Person;
import ru.skillbox.socialnet.repository.PersonRepository;
import ru.skillbox.socialnet.security.JwtTokenUtils;

@ContextConfiguration(initializers = {GeolocationsControllerTest.Initializer.class})
@Testcontainers
@SpringBootTest
@AutoConfigureMockMvc
@Sql(value = {"/dialogs-before-data.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
class GeolocationsControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private PersonRepository personRepository;
    @Autowired
    private JwtTokenUtils jwtTokenUtils;
    @Autowired
    private ObjectMapper objectMapper;

    private String token;

    @Container
    private static PostgreSQLContainer<?> postgreSQLContainer =
        new PostgreSQLContainer<>("postgres:latest")
            .withDatabaseName("socialnet-dialogs-test")
            .withUsername("test")
            .withPassword("test");


    static class Initializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {
        public void initialize(ConfigurableApplicationContext configurableApplicationContext) {
            TestPropertyValues.of(
                "spring.datasource.url=" + postgreSQLContainer.getJdbcUrl(),
                "spring.datasource.username=" + postgreSQLContainer.getUsername(),
                "spring.datasource.password=" + postgreSQLContainer.getPassword(),
                "spring.liquibase.enabled=true"
            ).applyTo(configurableApplicationContext.getEnvironment());
        }
    }
    @Test
    void getCountries() throws Exception {

        String token = getToken(1L);

        var result = this.mockMvc.perform(get("/api/v1/geolocations/countries")
                .header("authorization", token)
            )
            .andDo(print())
            .andExpect(status().isOk())
            .andReturn();

        var response = result.getResponse();
        var res = objectMapper.readValue(response.getContentAsString(StandardCharsets.UTF_8), CommonRs.class);
        var data = (List<LinkedHashMap<String,String>>) res.getData();
        assertFalse(data.isEmpty());
        assertTrue(data.stream().anyMatch(t->t.get("title").equals("Россия")));

    }

    @Test
    void getCitiesUses() throws Exception {
        String token = getToken(1L);

        var result = this.mockMvc.perform(get("/api/v1/geolocations/cities/uses")
                .header("authorization", token)
                .param("country", "Россия")
            )
            .andDo(print())
            .andExpect(status().isOk())
            .andReturn();

        var response = result.getResponse();
        var res = objectMapper.readValue(response.getContentAsString(StandardCharsets.UTF_8), CommonRs.class);
        var data = (List<LinkedHashMap<String,String>>) res.getData();
        assertTrue(data.isEmpty());
    }

    @Test
    void getCitiesDB() throws Exception {
        String token = getToken(1L);

        var result = this.mockMvc.perform(get("/api/v1/geolocations/cities/db")
                .header("authorization", token)
                .param("country", "Россия")
                .param("starts", "Пермь")
            )
            .andDo(print())
            .andExpect(status().isOk())
            .andReturn();

        var response = result.getResponse();
        var res = objectMapper.readValue(response.getContentAsString(StandardCharsets.UTF_8), CommonRs.class);
        var data = (List<LinkedHashMap<String,String>>) res.getData();
        assertTrue(data.isEmpty());
    }

    @Test
    void getCitiesApi() throws Exception {
        String token = getToken(1L);

        var countriesResult = this.mockMvc.perform(get("/api/v1/geolocations/countries")
                .header("authorization", token)
            )
            .andDo(print())
            .andExpect(status().isOk())
            .andReturn();

        var result = this.mockMvc.perform(get("/api/v1/geolocations/cities/api")
                .header("authorization", token)
                .param("country", "Россия")
                .param("starts", "Пермь")
            )
            .andDo(print())
            .andExpect(status().isOk())
            .andReturn();

        var response = result.getResponse();
        var res = objectMapper.readValue(response.getContentAsString(StandardCharsets.UTF_8), CommonRs.class);
        var data = (List<LinkedHashMap<String,String>>) res.getData();

        assertFalse(data.isEmpty());
        assertTrue(data.stream().anyMatch(t->t.get("title").equals("Пермь")));
    }

    private String getToken(Long userId) {

        if (token != null) {
            return token;
        }

        Person person = personRepository.findById(userId).get();
        String token = jwtTokenUtils.generateToken(person);
        this.token = token;

        return token;
    }
}