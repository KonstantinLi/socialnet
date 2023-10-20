package ru.skillbox.socialnet;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
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
import ru.skillbox.socialnet.dto.request.CommentRq;
import ru.skillbox.socialnet.entity.personrelated.Person;
import ru.skillbox.socialnet.entity.postrelated.PostComment;
import ru.skillbox.socialnet.repository.PersonRepository;
import ru.skillbox.socialnet.repository.PostCommentsRepository;
import ru.skillbox.socialnet.security.JwtTokenUtils;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SuppressWarnings("ALL")
@Slf4j
@ContextConfiguration(initializers = {PostCommentsControllerTest.Initializer.class})
@Testcontainers
@SpringBootTest
@AutoConfigureMockMvc
@Sql(value = {"/post-comments-before-data.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
class PostCommentsControllerTest {
    @Autowired
    private PostCommentsRepository postCommentsRepository;

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
                    .withDatabaseName("socialnet-post-comments-test")
                    .withUsername("root")
                    .withPassword("root");

    static class Initializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {
        public void initialize(ConfigurableApplicationContext configurableApplicationContext) {
            TestPropertyValues.of("spring.datasource.url=" + postgreSQLContainer.getJdbcUrl(),
                    "spring.datasource.username=" + postgreSQLContainer.getUsername(),
                    "spring.datasource.password=" + postgreSQLContainer.getPassword(),
                    "spring.liquibase.enabled=true",
                    "spring.liquibase.change-log=classpath:db/changelog/v1/001_init_schema.yaml"
            ).applyTo(configurableApplicationContext.getEnvironment());
        }
    }

    @Test
    void editComment() throws Exception {
        Iterable<PostComment> all = postCommentsRepository.findAll();
        all.forEach(postComment -> log.info("{}", postComment.getId()));

        CommentRq commentRq = new CommentRq();
        commentRq.setParentId(100L);
        commentRq.setCommentText("New comment text");
        commentRq.setIsDeleted(false);

        mockMvc.perform(put("/api/v1/post/{id}/comments/{comment_id}", 10, 101)
                        .header("Authorization", getToken())
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(commentRq)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.total").value(1))
                .andExpect(jsonPath("$.data[0].id").value(101))
                .andExpect(jsonPath("$.data[0].comment_text").value("New comment text"))
                .andExpect(jsonPath("$.data[0].author.id").value(3));
    }

    @Test
    void getComments() throws Exception {
        long postId = 10L;

        mockMvc.perform(get("/api/v1/post/{id}/comments", postId)
                        .header("Authorization", getToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("total").value(2))

                .andExpect(jsonPath("$.data.length()").value(2))
                .andExpect(jsonPath("$.data[*].id", containsInAnyOrder(100, 101)))

                .andExpect(jsonPath("$.data[?(@.id == 100)].comment_text")
                        .value("Parent comment text"))
                .andExpect(jsonPath("$.data[?(@.id == 100)].author.id")
                        .value(2))

                .andExpect(jsonPath("$.data[?(@.id == 101)].comment_text")
                        .value("Sub comment text"))
                .andExpect(jsonPath("$.data[?(@.id == 101)].author.id")
                        .value(3))

                .andExpect(jsonPath("$.data[?(@.id == 100)].sub_comments.length()")
                        .value(1))
                .andExpect(jsonPath("$.data[?(@.id == 100)].sub_comments[0].id")
                        .value(101))
                .andExpect(jsonPath("$.data[?(@.id == 100)].sub_comments[0].comment_text")
                        .value("Sub comment text"));
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
}
