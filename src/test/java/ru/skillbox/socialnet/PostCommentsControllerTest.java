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
import org.springframework.test.web.servlet.MvcResult;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import ru.skillbox.socialnet.dto.request.LikeRq;
import ru.skillbox.socialnet.dto.response.CommentRs;
import ru.skillbox.socialnet.dto.response.CommonRs;
import ru.skillbox.socialnet.dto.response.PersonRs;
import ru.skillbox.socialnet.entity.personrelated.Person;
import ru.skillbox.socialnet.mapper.PersonMapper;
import ru.skillbox.socialnet.repository.PersonRepository;
import ru.skillbox.socialnet.security.JwtTokenUtils;
import ru.skillbox.socialnet.service.PostCommentsService;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SuppressWarnings({"OptionalGetWithoutIsPresent", "unused"})
@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@Sql(value = {"/likes-test-data.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
public class PostCommentsControllerTest {

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
    public void getComments() throws Exception {
        Person person = personRepository.findById(1L).get();
        String token = jwtTokenUtils.generateToken(person);

        long postId = 10L;

        CommonRs<List<CommentRs>> expectedResult = createExpectedGetCommentsResponse(postId);

        MvcResult mocRequestResult = this.mockMvc.perform(get("/api/v1/post/" + postId + "/comments")
                        .header("authorization", token))
                .andExpect(status().isOk()).andReturn();
        String actualResultAsString = mocRequestResult.getResponse().getContentAsString();

        //noinspection unchecked
        CommonRs<List<CommentRs>> actualResult = objectMapper.readValue(actualResultAsString, CommonRs.class);

        //TODO assert сделан неправильно, нужно переделать
        assert expectedResult.getTotal().equals(actualResult.getTotal());
        assert expectedResult.getData().equals(actualResult.getData());
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
