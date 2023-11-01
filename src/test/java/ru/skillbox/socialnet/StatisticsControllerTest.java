package ru.skillbox.socialnet;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
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
import ru.skillbox.socialnet.entity.enums.LikeType;
import ru.skillbox.socialnet.repository.*;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@Slf4j
@AutoConfigureMockMvc
@Testcontainers
@ContextConfiguration(initializers = {StatisticsControllerTest.Initializer.class})
@Sql(value = {"/stat-before-data.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@DisplayName("Statistics API Test")
class StatisticsControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private PersonRepository personRepository;
    @Autowired
    private TagsRepository tagsRepository;
    @Autowired
    private PostsRepository postsRepository;
    @Autowired
    private PostCommentsRepository postCommentsRepository;
    @Autowired
    private MessagesRepository messagesRepository;
    @Autowired
    private LikesRepository likesRepository;
    @Autowired
    private DialogsRepository dialogsRepository;
    @Autowired
    private CountriesRepository countriesRepository;
    @Autowired
    private CitiesRepository citiesRepository;

    @Container
    public static PostgreSQLContainer<?> postgreSqlContainer = new PostgreSQLContainer<>("postgres:15.1")
            .withDatabaseName("socialnet_test")
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

            log.info(configurableApplicationContext.getEnvironment().getProperty("spring.datasource.url"));
        }
    }

    @Test
    @DisplayName("Users Statistics")
    void test_01() throws Exception {
        this.mockMvc.perform(get("/api/v1/statistics/user"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(String.valueOf(
                        personRepository.countByIsDeleted(false)
                )));

        this.mockMvc.perform(get("/api/v1/statistics/user/country")
                        .queryParam("country", "Russia")
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string("0"));

        this.mockMvc.perform(get("/api/v1/statistics/user/city")
                        .queryParam("city", "Padova")
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string("0"));
    }

    @Test
    @DisplayName("Tags Statistics")
    void test_02() throws Exception {
        this.mockMvc.perform(get("/api/v1/statistics/tag"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(String.valueOf(
                        tagsRepository.count()
                )));

        this.mockMvc.perform(get("/api/v1/statistics/tag/post")
                        .queryParam("postId", "50")
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string("0"));
    }

    @Test
    @DisplayName("Posts Statistics")
    void test_03() throws Exception {
        this.mockMvc.perform(get("/api/v1/statistics/post"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(String.valueOf(
                        postsRepository.countByIsDeleted(false)
                )));

        this.mockMvc.perform(get("/api/v1/statistics/post/user")
                        .queryParam("userId", "12")
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(String.valueOf(
                        postsRepository.countByAuthorIdAndIsDeleted(12, false)
                )));
    }

    @Test
    @DisplayName("Messages Statistics")
    void test_04() throws Exception {
        this.mockMvc.perform(get("/api/v1/statistics/message"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(String.valueOf(
                        messagesRepository.countByIsDeleted(false)
                )));

        this.mockMvc.perform(get("/api/v1/statistics/message/dialog")
                        .queryParam("dialogId", "1")
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(String.valueOf(
                        messagesRepository.countByDialogIdAndIsDeleted(1, false)
                )));

        this.mockMvc.perform(get("/api/v1/statistics/message/all")
                        .queryParam("firstUserId", "1")
                        .queryParam("secondUserId", "2")
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$.Ella_Chaplin->Adriaens_Whillock").value(0)
                )
                .andExpect(
                        jsonPath("$.Adriaens_Whillock->Ella_Chaplin").value(0)
                );
    }

    @Test
    @DisplayName("Likes Statistics")
    void test_05() throws Exception {
        this.mockMvc.perform(get("/api/v1/statistics/like"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(String.valueOf(
                        likesRepository.count()
                )));

        this.mockMvc.perform(get("/api/v1/statistics/like/entity")
                        .queryParam("type", "Post")
                        .queryParam("entityId", "1")
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(String.valueOf(
                        likesRepository.countByTypeAndEntityId(LikeType.Post, 1)
                )));
    }

    @Test
    @DisplayName("Dialogs Statistics")
    void test_06() throws Exception {
        this.mockMvc.perform(get("/api/v1/statistics/dialog"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(String.valueOf(
                        dialogsRepository.count()
                )));

        this.mockMvc.perform(get("/api/v1/statistics/dialog/user")
                        .queryParam("userId", "3")
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(String.valueOf(
                        dialogsRepository.countByFirstPersonIdOrSecondPersonId(3L, 3L)
                )));
    }

    @Test
    @DisplayName("Countries Statistics")
    void test_07() throws Exception {
        this.mockMvc.perform(get("/api/v1/statistics/country"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(String.valueOf(
                        countriesRepository.count()
                )));

        this.mockMvc.perform(get("/api/v1/statistics/country/all"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$[0].region").value("Nicaragua")
                )
                .andExpect(
                        jsonPath("$[0].countUsers").value(1)
                );
    }

    @Test
    @DisplayName("Comments Statistics")
    void test_08() throws Exception {
        this.mockMvc.perform(get("/api/v1/statistics/comment/post")
                        .queryParam("postId", "1")
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(String.valueOf(
                        postCommentsRepository.countByPostIdAndIsDeleted(1, false)
                )));
    }

    @Test
    @DisplayName("Cities Statistics")
    void test_09() throws Exception {
        this.mockMvc.perform(get("/api/v1/statistics/city"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(String.valueOf(
                        citiesRepository.count()
                )));

        this.mockMvc.perform(get("/api/v1/statistics/city/all"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$[0].region").value("Dobczyce")
                )
                .andExpect(
                        jsonPath("$[0].countUsers").value(1)
                );
    }
}
