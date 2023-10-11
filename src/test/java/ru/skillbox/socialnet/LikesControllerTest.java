package ru.skillbox.socialnet;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import net.minidev.json.JSONArray;
import net.minidev.json.parser.JSONParser;
import net.minidev.json.parser.ParseException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import ru.skillbox.socialnet.dto.request.LikeRq;
import ru.skillbox.socialnet.entity.enums.LikeType;
import ru.skillbox.socialnet.entity.personrelated.Person;
import ru.skillbox.socialnet.entity.postrelated.Like;
import ru.skillbox.socialnet.repository.PersonRepository;
import ru.skillbox.socialnet.security.JwtTokenUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SuppressWarnings("OptionalGetWithoutIsPresent")
@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@Sql(value = {"/likes-test-data.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
public class LikesControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private PersonRepository personRepository;
    @Autowired
    private JwtTokenUtils jwtTokenUtils;

    @SuppressWarnings("resource")
    @Container
    private static PostgreSQLContainer<?> postgreSQLContainer =
            new PostgreSQLContainer<>("postgres:15.1")
                    .withDatabaseName("socialnet-likes-test")
                    .withUsername("test")
                    .withPassword("test");

    @SuppressWarnings("unused")
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
    public void contextLoads() {
    }

    @Test
    public void getLikes() throws Exception {
        Person person = personRepository.findById(1L).get();
        String token = jwtTokenUtils.generateToken(person);

        List<Long> usersIds = Arrays.asList(1L, 2L, 4L);
        int expectedLikes = usersIds.size();
        JSONArray expectedUsersIdsPost = convertToJSONArray(usersIds);

        this.mockMvc.perform(get("/api/v1/likes")
                        .header("authorization", token)
                        .param("item_id", "10")
                        .param("type", "POST"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.likes").value(expectedLikes))
                .andExpect(jsonPath("$.data.users").value(expectedUsersIdsPost));
    }

    @Test
    public void putLike() throws Exception {
        Person person = personRepository.findById(1L).get();
        String token = jwtTokenUtils.generateToken(person);

        LikeRq likeRq = new LikeRq();
        likeRq.setType(LikeType.COMMENT);
        likeRq.setItemId(101L);
        String likeRqJSON = convertToJSONString(likeRq);

        List<Long> usersIds = Arrays.asList(1L, 2L);
        int expectedLikes = usersIds.size();
        JSONArray expectedUsersIdsPost = convertToJSONArray(usersIds);

        this.mockMvc.perform(put("/api/v1/likes")
                        .header("authorization", token)
                        .content(likeRqJSON)
                        .contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.likes").value(expectedLikes))
                .andExpect(jsonPath("$.data.users").value(expectedUsersIdsPost));
    }

    @Test
    public void deleteLike() throws Exception {
        Person person = personRepository.findById(1L).get();
        String token = jwtTokenUtils.generateToken(person);

        List<Long> usersIds = Arrays.asList(2L, 4L);
        int expectedLikes = usersIds.size();
        JSONArray expectedUsersIdsPost = convertToJSONArray(usersIds);

        this.mockMvc.perform(delete("/api/v1/likes")
                        .header("authorization", token)
                        .param("item_id", "10")
                        .param("type", "POST"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.likes").value(expectedLikes))
                .andExpect(jsonPath("$.data.users").value(expectedUsersIdsPost));
    }

    //TODO move to distinct utils class
    private JSONArray convertToJSONArray(List<Long> values)
            throws ParseException, JsonProcessingException {

        JSONParser jsonParser = new JSONParser(JSONParser.DEFAULT_PERMISSIVE_MODE);
        ObjectMapper objectMapper = new ObjectMapper();

        return (JSONArray) jsonParser.parse(objectMapper.writeValueAsString(values));
    }

    private String convertToJSONString(LikeRq likeRq) throws JsonProcessingException {

        ObjectMapper objectMapper = new ObjectMapper();

        return objectMapper.writeValueAsString(likeRq);
    }
}
