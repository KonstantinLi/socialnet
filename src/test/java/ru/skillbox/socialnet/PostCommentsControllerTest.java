package ru.skillbox.socialnet;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
import ru.skillbox.socialnet.dto.response.CommentRs;
import ru.skillbox.socialnet.dto.response.CommonRs;
import ru.skillbox.socialnet.dto.response.PersonRs;
import ru.skillbox.socialnet.entity.personrelated.Person;
import ru.skillbox.socialnet.entity.postrelated.PostComment;
import ru.skillbox.socialnet.mapper.PersonMapper;
import ru.skillbox.socialnet.repository.PersonRepository;
import ru.skillbox.socialnet.repository.PersonSettingsRepository;
import ru.skillbox.socialnet.repository.PostCommentsRepository;
import ru.skillbox.socialnet.repository.PostsRepository;
import ru.skillbox.socialnet.security.JwtTokenUtils;
import ru.skillbox.socialnet.service.PostCommentsService;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SuppressWarnings("ALL")
@Slf4j
@ContextConfiguration(initializers = {PostCommentsControllerTest.Initializer.class})
@Testcontainers
@SpringBootTest
@AutoConfigureMockMvc
@Sql(value = {"/likes-test-data.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
//@Sql(value = {"/tests-after.sql"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
public class PostCommentsControllerTest {
    @Autowired
    private PostsRepository postsRepository;
    @Autowired
    private PersonSettingsRepository personSettingsRepository;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PersonRepository personRepository;

    @Autowired
    private JwtTokenUtils jwtTokenUtils;

    @Autowired
    private PostCommentsService postCommentsService;

    @Autowired
    private PersonMapper personMapper;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PostCommentsRepository postCommentsRepository;
    @Container
    private static PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>("postgres:15.1")
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
    public void contextLoads() throws JsonProcessingException {
        List<Person> all = personRepository.findAll();
        for (Person person : all) {
            log.info("person: {}", person.getEmail());
        }

        personSettingsRepository.findAll().forEach(personSettings ->
                log.info("personSettings: {}", personSettings.getId()));
    }

    @Test
    public void getComments() throws Exception {
        postsRepository.findAll().forEach(post ->
                log.info("post: {}", post.getId()));

        Person person = personRepository.findById(1L).get();
        String token = jwtTokenUtils.generateToken(person);

        long postId = 12L;

        long parentCommentId = 104L;
        long parentCommentAuthorId = 3L;
        String parentCommentText = "Parent comment text";

        long subCommentId = 106L;
        long subCommentAuthorId = 2L;
        String subCommentText = "Sub comment text";

        mockMvc.perform(get("/api/v1/post/{id}/comments", postId)
                        .header("Authorization", token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].id").value(parentCommentId))
                .andExpect(jsonPath("$.data[0].author.id").value(parentCommentAuthorId))
                .andExpect(jsonPath("$.data[0].comment_text").value(parentCommentText))
                .andExpect(jsonPath("$.data[0].sub_comments[0].id").value(subCommentId))
                .andExpect(jsonPath("$.data[0].sub_comments[0].author.id").value(subCommentAuthorId))
                .andExpect(jsonPath("$.data[0].sub_comments[0].comment_text").value(subCommentText))
                .andExpect(jsonPath("$.data[1].id").value(subCommentId));
    }

    private CommonRs<List<CommentRs>> createExpectedGetCommentsResponse(long postId) {

        Person parentCommentAuthor = personRepository.findById(3L).get();
        PersonRs parrentCommentPersonRs = personMapper.personToPersonRs(parentCommentAuthor);

        Person subCommentAuthor = personRepository.findById(2L).get();
        PersonRs subCommentPersonRs = personMapper.personToPersonRs(subCommentAuthor);

        CommentRs subCommentRs = new CommentRs();
        subCommentRs.setAuthor(subCommentPersonRs);
        subCommentRs.setCommentText("Sub comment text");
        subCommentRs.setId(106L);
        subCommentRs.setIsBlocked(false);
        subCommentRs.setIsDeleted(false);
        subCommentRs.setLikes(0);
        subCommentRs.setMyLike(false);
        subCommentRs.setParentId(104L);
        subCommentRs.setPostId(postId);
        subCommentRs.setSubComments(null);
        subCommentRs.setTime("2023-04-19 10:42:27");

        CommentRs parentCommentRs = new CommentRs();
        parentCommentRs.setAuthor(parrentCommentPersonRs);
        parentCommentRs.setCommentText("Parent comment text");
        parentCommentRs.setId(104L);
        parentCommentRs.setIsBlocked(false);
        parentCommentRs.setIsDeleted(false);
        parentCommentRs.setLikes(0);
        parentCommentRs.setMyLike(false);
        parentCommentRs.setParentId(null);
        parentCommentRs.setPostId(postId);
        parentCommentRs.setSubComments(new HashSet<>(List.of(subCommentRs)));
        parentCommentRs.setTime("2023-09-17 00:43:56");

        List<CommentRs> expectedCommentRsList = Arrays.asList(subCommentRs, parentCommentRs);
        CommonRs<List<CommentRs>> expectedCommentRsCommonRs = new CommonRs<>();
        expectedCommentRsCommonRs.setTotal((long) expectedCommentRsList.size());
        expectedCommentRsCommonRs.setData(expectedCommentRsList);

        return expectedCommentRsCommonRs;
    }


    //TODO move to distinct utils class
    private JSONArray convertToJSONArray(List<Long> values) throws JsonProcessingException, ParseException {

        JSONParser jsonParser = new JSONParser(JSONParser.DEFAULT_PERMISSIVE_MODE);
        ObjectMapper objectMapper = new ObjectMapper();

        return (JSONArray) jsonParser.parse(objectMapper.writeValueAsString(values));
    }

    private String convertToJSONString(LikeRq likeRq) throws JsonProcessingException {

        ObjectMapper objectMapper = new ObjectMapper();

        return objectMapper.writeValueAsString(likeRq);
    }
}
