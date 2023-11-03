package ru.skillbox.socialnet;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
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
import ru.skillbox.socialnet.controller.PostsController;
import ru.skillbox.socialnet.dto.request.PostRq;
import ru.skillbox.socialnet.entity.personrelated.Person;
import ru.skillbox.socialnet.entity.postrelated.Post;
import ru.skillbox.socialnet.repository.PersonRepository;
import ru.skillbox.socialnet.repository.PostsRepository;
import ru.skillbox.socialnet.security.JwtTokenUtils;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SuppressWarnings("ALL")
@Slf4j
@SpringBootTest
@ContextConfiguration(initializers = {PostControllerTests.Initializer.class})
@Testcontainers
@AutoConfigureMockMvc
//@TestPropertySource("/application-test.yml")
@Sql(value = {"/post-before-data.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(value = {"/post-after-data.sql"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
class PostControllerTests {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private PostsController postsController;
    @Autowired
    private JwtTokenUtils jwtTokenUtils;
    @Autowired
    private PersonRepository personRepository;
    @Autowired
    private PostsRepository postsRepository;
    @Autowired
    private ObjectMapper objectMapper;

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
    void getPostById() throws Exception {
        Person person = personRepository.findById(Long.valueOf(1)).get();
        String token = jwtTokenUtils.generateToken(person);

        this.mockMvc.perform(get("/api/v1/post/2").header("authorization", token))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.title").value("post 2"))
                .andExpect(jsonPath("$.data.post_text").value("text test post 2"));
    }

    @Test
    void getPostByIdBadRequestNotFoundPost() throws Exception {
        Person person = personRepository.findById(Long.valueOf(1)).get();
        String token = jwtTokenUtils.generateToken(person);

        this.mockMvc.perform(get("/api/v1/post/3").header("authorization", token))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error_description").value("Post id 3 not found"));
    }

    @Test
    void getPostByIdBadRequestUnauthorized() throws Exception {
        Person person = personRepository.findById(Long.valueOf(2)).get();
        String token = jwtTokenUtils.generateToken(person);
        token = token.replaceAll("a", "b");
        this.mockMvc.perform(get("/api/v1/post/1").header("authorization", token))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    void updateById() throws Exception {

        Person person = personRepository.findById(1L).get();
        String token = jwtTokenUtils.generateToken(person);
        String requestBody = "{\n" +
                "  \"title\": \"NEW TITLE\",\n" +
                "  \"post_text\": \"some new text\"\n" +
                "}";

        Post post = postsRepository.findById(Long.valueOf(2)).get();
        this.mockMvc.perform(put("/api/v1/post/2")
                        .content(requestBody)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("authorization", token))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.title").value("NEW TITLE"))
                .andExpect(jsonPath("$.data.post_text").value("some new text"));
    }

    @Test
    void updateByIdEmptyField() throws Exception {

        Person person = personRepository.findById(Long.valueOf(1)).get();
        String token = jwtTokenUtils.generateToken(person);
        String requestBody = "{\n" +
                "  \"title\": \"NEW TITLE\",\n" +
                "  \"post_text\": \"some new text\"\n" +
                "}";

        Post post = postsRepository.findById(Long.valueOf(2)).get();
        this.mockMvc.perform(put("/api/v1/post/3")
                        .content(requestBody)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("authorization", token))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error_description").value("Post id 3 not found"));
    }

    @Test
    void deleteById() throws Exception {
        Person person = personRepository.findById(Long.valueOf(1)).get();
        String token = jwtTokenUtils.generateToken(person);

        this.mockMvc.perform(delete("/api/v1/post/2").header("authorization", token))
                .andDo(print())
                .andExpect(status().isOk());

        Post post = postsRepository.findById(Long.valueOf(1)).get();
        Assert.assertTrue(post.getIsDeleted());

        this.mockMvc.perform(get("/api/v1/post/2").header("authorization", token))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.type").value("DELETED"));
    }

    @Test
    void deleteByIdBadRequest() throws Exception {
        Person person = personRepository.findById(Long.valueOf(1)).get();
        String token = jwtTokenUtils.generateToken(person);

        this.mockMvc.perform(delete("/api/v1/post/3").header("authorization", token))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error_description").value("Post id 3 not found"));
    }

    @Test
    void recoverPostById() throws Exception {
        Person person = personRepository.findById(Long.valueOf(1)).get();
        String token = jwtTokenUtils.generateToken(person);

        this.mockMvc.perform(delete("/api/v1/post/2").header("authorization", token))
                .andDo(print())
                .andExpect(status().isOk());

        Post post = postsRepository.findById(Long.valueOf(2)).get();
        Assert.assertTrue(post.getIsDeleted());

        this.mockMvc.perform(put("/api/v1/post/2/recover").header("authorization", token))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.type").value("POSTED"));

        post = postsRepository.findById(Long.valueOf(2)).get();
        Assert.assertFalse(post.getIsDeleted());
    }

    @Test
    void recoverPostByIdBadRequest() throws Exception {
        Person person = personRepository.findById(Long.valueOf(1)).get();
        String token = jwtTokenUtils.generateToken(person);

        this.mockMvc.perform(put("/api/v1/post/3/recover").header("authorization", token))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error_description").value("Post id 3 not found"));
    }

    @Test
    void getWall() throws Exception {
        Person person = personRepository.findById(Long.valueOf(1)).get();
        String token = jwtTokenUtils.generateToken(person);

        this.mockMvc.perform(get("/api/v1/users/1/wall")
                        .header("authorization", token)
                        .param("offset", "3")
                        .param("perPage", "9"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.offset").value("3"))
                .andExpect(jsonPath("$.perPage").value("0"));
    }

    @Test
    void getWallBadRequest() throws Exception {
        Person person = personRepository.findById(Long.valueOf(1)).get();
        String token = jwtTokenUtils.generateToken(person);

        this.mockMvc.perform(get("/api/v1/users/3/wall")
                        .header("authorization", token)
                        .param("offset", "3")
                        .param("perPage", "9"))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error_description").value("Person id 3 not found"));
    }

    @Test
    void сreatePost() throws Exception {
        Person person = personRepository.findById(Long.valueOf(1)).get();
        String token = jwtTokenUtils.generateToken(person);

        PostRq postRq = new PostRq();
        postRq.setTitle("NEW TITLE СreatePost");
        postRq.setPostText("some new text СreatePost");
        ObjectMapper objectMapper = new ObjectMapper();
        String requestBody = objectMapper.writeValueAsString(postRq);

        this.mockMvc.perform(post("/api/v1/users/1/wall")
                        .content(requestBody)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("authorization", token))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.title").value("NEW TITLE СreatePost"))
                .andExpect(jsonPath("$.data.post_text").value("some new text СreatePost"));
    }

    @Test
    void сreatePostBadRequest() throws Exception {
        Person person = personRepository.findById(Long.valueOf(1)).get();
        String token = jwtTokenUtils.generateToken(person);

        PostRq postRq = new PostRq();
        postRq.setTitle("");
        postRq.setPostText("some new text СreatePost");
        ObjectMapper objectMapper = new ObjectMapper();
        String requestBody = objectMapper.writeValueAsString(postRq);

        this.mockMvc.perform(post("/api/v1/users/1/wall")
                        .content(requestBody)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("authorization", token))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error_description").value("Post title is absent"));
    }


    @Test
    void getPostsByQuery() throws Exception {
        Person person = personRepository.findById(Long.valueOf(2)).get();
        String token = jwtTokenUtils.generateToken(person);

        this.mockMvc.perform(get("/api/v1/post")
                        .header("authorization", token)
                        .param("author", "Adriaens")
                        .param("text", "text test post 2")
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].title").value("post 2"))
                .andExpect(jsonPath("$.data[0].post_text").value("text test post 2"));
    }

    @Test
    void getFeeds() throws Exception {
        Person person = personRepository.findById(Long.valueOf(1)).get();
        String token = jwtTokenUtils.generateToken(person);

        PostRq postRq = new PostRq();
        postRq.setTitle("NEW TITLE СreatePost");
        postRq.setPostText("some new text СreatePost");
        ObjectMapper objectMapper = new ObjectMapper();
        String requestBody = objectMapper.writeValueAsString(postRq);

        this.mockMvc.perform(post("/api/v1/users/1/wall")
                        .content(requestBody)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("authorization", token))
                .andDo(print())
                .andExpect(status().isOk());

        this.mockMvc.perform(get("/api/v1/feeds")
                        .header("authorization", token)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].title").value("NEW TITLE СreatePost"))
                .andExpect(jsonPath("$.data[0].post_text").value("some new text СreatePost"));
    }

}

