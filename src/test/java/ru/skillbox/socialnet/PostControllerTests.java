package ru.skillbox.socialnet;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
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
@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
//@TestPropertySource("/application-test.yml")
@Sql(value = {"/post-before-data.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
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
                    "pring.datasource.url=" + postgreSqlContainer.getJdbcUrl(),
                    "spring.datasource.username=" + postgreSqlContainer.getUsername(),
                    "spring.datasource.password=" + postgreSqlContainer.getPassword(),
                    "spring.liquibase.enabled=true"
            ).applyTo(configurableApplicationContext.getEnvironment());
        }
    }

    @Test
    void contextLoads() throws Exception {
    }

    @Test
    void getPostById() throws Exception {
        Person person = personRepository.findById(Long.valueOf(1)).get();
        String token = jwtTokenUtils.generateToken(person);

        this.mockMvc.perform(get("/api/v1/post/1").header("authorization", token))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.title").value("post 1"))
                .andExpect(jsonPath("$.data.post_text").value("text test post 1"));
    }

    @Test
    public void getPostByIdBadRequestNotFoundPost() throws Exception {
        Person person = personRepository.findById(Long.valueOf(1)).get();
        String token = jwtTokenUtils.generateToken(person);

        this.mockMvc.perform(get("/api/v1/post/3").header("authorization", token))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error_description").value("Post id 3 not found"));
    }

    @Test
    public void getPostByIdBadRequestUnauthorized() throws Exception {
        Person person = personRepository.findById(Long.valueOf(2)).get();
        String token = jwtTokenUtils.generateToken(person);
        token = token.replaceAll("a", "b");
        this.mockMvc.perform(get("/api/v1/post/1").header("authorization", token))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void updateById() throws Exception {

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
    public void updateByIdEmptyField() throws Exception {

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

//    deleteById

    @Test
    public void deleteById() throws Exception {
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
    public void deleteByIdBadRequest() throws Exception {
        Person person = personRepository.findById(Long.valueOf(1)).get();
        String token = jwtTokenUtils.generateToken(person);

        this.mockMvc.perform(delete("/api/v1/post/3").header("authorization", token))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error_description").value("Post id 3 not found"));
    }

    @Test
    public void recoverById() throws Exception {
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
    public void recoverByIdBadRequest() throws Exception {
        Person person = personRepository.findById(Long.valueOf(1)).get();
        String token = jwtTokenUtils.generateToken(person);

        this.mockMvc.perform(put("/api/v1/post/3/recover").header("authorization", token))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error_description").value("Post id 3 not found"));
    }

    @Test
    public void getWall() throws Exception {
        Person person = personRepository.findById(Long.valueOf(1)).get();
        String token = jwtTokenUtils.generateToken(person);

        this.mockMvc.perform(get("/api/v1/users/1/wall")
                        .header("authorization", token)
                        .param("offset", "3")
                        .param("perPage", "9"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.offset").value("3"))
                .andExpect(jsonPath("$.perPage").value("9"));
    }

    @Test
    public void getWallBadRequest() throws Exception {
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
    public void getWallСreatePost() throws Exception {
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
    public void getWallСreatePostBadRequest() throws Exception {
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
    public void getPostsByQuery() throws Exception {
        Person person = personRepository.findById(Long.valueOf(2)).get();
        String token = jwtTokenUtils.generateToken(person);

        this.mockMvc.perform(get("/api/v1/post")
                                .header("authorization", token)
//                        .param("author", "")
//                        .param("date_from", "")
//                        .param("dateTo", String.valueOf(new Date().getTime()))
                                .param("text", "text test post 3")
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].title").value("post 2"))
                .andExpect(jsonPath("$.data[0].post_text").value("text test post 2"));
    }

//    @Test
//    public void getPostsByQueryBadRequest() throws Exception {
//        Person person = personRepository.findById(Long.valueOf(3)).get();
//        String token = jwtTokenUtils.generateToken(person);
//
//        this.mockMvc.perform(get("/api/v1/post")
//                        .header("authorization", token)
//                        .param("text", "text test post 3")
//                )
//                .andDo(print())
//                .andExpect(status().isBadRequest())
//                .andExpect(jsonPath("$.error_description").value("Person id 3 not found"));
//    }

    @Test
    public void getFeeds() throws Exception {
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
//                        .param("offset", "1")
//                        .param("perPage", "1")
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].title").value("NEW TITLE СreatePost"))
                .andExpect(jsonPath("$.data[0].post_text").value("some new text СreatePost"));
    }

//    @Test
//    public void getFeedsBadRequest() throws Exception {
//        //todo что является ошибкой?
//        Person person = personRepository.findById(Long.valueOf(1)).get();
//        String token = jwtTokenUtils.generateToken(person);
//
////        this.mockMvc.perform(delete("/api/v1/post/2").header("authorization", token))
////                .andDo(print())
////                .andExpect(status().isOk());
//
//        this.mockMvc.perform(get("/api/v1/feeds")
//                                .header("authorization", token)
//                )
//                .andDo(print())
//                .andExpect(status().isBadRequest())
//                .andExpect(jsonPath("$.error_description").value("Person id 3 not found"));
//    }
}

