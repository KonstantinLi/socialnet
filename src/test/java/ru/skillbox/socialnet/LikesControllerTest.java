package ru.skillbox.socialnet;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.minidev.json.JSONArray;
import net.minidev.json.parser.JSONParser;
import net.minidev.json.parser.ParseException;
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
import ru.skillbox.socialnet.dto.request.LikeRq;
import ru.skillbox.socialnet.entity.enums.LikeType;
import ru.skillbox.socialnet.entity.personrelated.Person;
import ru.skillbox.socialnet.repository.PersonRepository;
import ru.skillbox.socialnet.security.JwtTokenUtils;

import java.util.Arrays;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SuppressWarnings("ALL")
@ContextConfiguration(initializers = {LikesControllerTest.Initializer.class})
@Testcontainers
@SpringBootTest
@AutoConfigureMockMvc
@Sql(value = {"/likes-before-data.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
class LikesControllerTest {

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
            new PostgreSQLContainer<>("postgres:15.1")
                    .withDatabaseName("socialnet-likes-test")
                    .withUsername("test")
                    .withPassword("test");


    static class Initializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {
        public void initialize(ConfigurableApplicationContext configurableApplicationContext) {
            TestPropertyValues.of(
                    "spring.datasource.url=" + postgreSQLContainer.getJdbcUrl(),
                    "spring.datasource.username=" + postgreSQLContainer.getUsername(),
                    "spring.datasource.password=" + postgreSQLContainer.getPassword(),
                    "spring.liquibase.enabled=true",
                    "spring.liquibase.change-log=classpath:db/changelog/v1/001_init_schema.yaml"
            ).applyTo(configurableApplicationContext.getEnvironment());
        }
    }

    @Test
    void getLikes() throws Exception {
        String token = getToken();

        long itemId = 10L;
        List<Long> usersIds = Arrays.asList(1L, 2L, 3L);
        int expectedLikes = usersIds.size();
        JSONArray expectedUsersIdsPost = convertToJSONArray(usersIds);

        this.mockMvc.perform(get("/api/v1/likes")
                        .header("authorization", token)
                        .param("item_id", String.valueOf(itemId))
                        .param("type", "POST"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.likes").value(expectedLikes))
                .andExpect(jsonPath("$.data.users").value(expectedUsersIdsPost));
    }

    @Test
    void putLike() throws Exception {
        String token = getToken();

        LikeRq likeRq = new LikeRq();
        likeRq.setType(LikeType.COMMENT);
        likeRq.setItemId(100L);
        String likeRqJSON = objectMapper.writeValueAsString(likeRq);

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
    void putLikeWithLikeRqEmptyFields() throws Exception {
        String token = getToken();

        LikeRq likeRq = new LikeRq();
        likeRq.setType(null);
        likeRq.setItemId(null);
        String likeRqJSON = objectMapper.writeValueAsString(likeRq);

        this.mockMvc.perform(put("/api/v1/likes")
                        .header("authorization", token)
                        .content(likeRqJSON)
                        .contentType("application/json"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error_description").isNotEmpty());
    }

    @Test
    void putLikeAlreadyExists() throws Exception {
        String token = getToken();

        LikeRq likeRq = new LikeRq();
        likeRq.setType(LikeType.POST);
        likeRq.setItemId(10L);
        String likeRqJSON = objectMapper.writeValueAsString(likeRq);

        this.mockMvc.perform(put("/api/v1/likes")
                        .header("authorization", token)
                        .content(likeRqJSON)
                        .contentType("application/json"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error_description").isNotEmpty());
    }

    @Test
    void putLikePostNotFound() throws Exception {
        String token = getToken();

        LikeRq likeRq = new LikeRq();
        likeRq.setType(LikeType.POST);
        likeRq.setItemId(400L);
        String likeRqJSON = objectMapper.writeValueAsString(likeRq);

        this.mockMvc.perform(put("/api/v1/likes")
                        .header("authorization", token)
                        .content(likeRqJSON)
                        .contentType("application/json"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error_description").isNotEmpty());
    }

    @Test
    void putLikeCommentNotFound() throws Exception {
        String token = getToken();

        LikeRq likeRq = new LikeRq();
        likeRq.setType(LikeType.COMMENT);
        likeRq.setItemId(400L);
        String likeRqJSON = objectMapper.writeValueAsString(likeRq);

        this.mockMvc.perform(put("/api/v1/likes")
                        .header("authorization", token)
                        .content(likeRqJSON)
                        .contentType("application/json"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error_description").isNotEmpty());
    }

    @Test
    void deleteLike() throws Exception {
        String token = getToken();

        long itemId = 10L;
        List<Long> usersIds = Arrays.asList(2L, 3L);
        int expectedLikes = usersIds.size();
        JSONArray expectedUsersIdsPost = convertToJSONArray(usersIds);

        this.mockMvc.perform(delete("/api/v1/likes")
                        .header("authorization", token)
                        .param("item_id", String.valueOf(itemId))
                        .param("type", "POST"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.likes").value(expectedLikes))
                .andExpect(jsonPath("$.data.users").value(expectedUsersIdsPost));
    }

    @Test
    void deleteLikeNotFound() throws Exception {
        String token = getToken();
        long itemId = 400L;

        this.mockMvc.perform(delete("/api/v1/likes")
                        .header("authorization", token)
                        .param("item_id", String.valueOf(itemId))
                        .param("type", "POST"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error_description").isNotEmpty());
    }

    private String getToken() {

        if (token != null) {
            return token;
        }

        Person person = personRepository.findById(1L).get();
        String token = jwtTokenUtils.generateToken(person);
        this.token = token;

        return token;
    }

    private JSONArray convertToJSONArray(List<Long> values) throws JsonProcessingException, ParseException {

        JSONParser jsonParser = new JSONParser(JSONParser.DEFAULT_PERMISSIVE_MODE);
        return (JSONArray) jsonParser.parse(objectMapper.writeValueAsString(values));
    }
}
