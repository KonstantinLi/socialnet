package ru.skillbox.socialnet;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import ru.skillbox.socialnet.entity.enums.LikeType;
import ru.skillbox.socialnet.repository.*;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DisplayName("Statistics API Test")
class StatisticsTest {
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
                .andExpect(content().string("1"));

        this.mockMvc.perform(get("/api/v1/statistics/user/city")
                        .queryParam("city", "Padova")
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string("1"));
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
                .andExpect(content().string("2"));
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
                        .queryParam("firstUserId", "3")
                        .queryParam("secondUserId", "2")
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$.Adriaens_Whillock->Ailsun_Asbury").value(0)
                )
                .andExpect(
                        jsonPath("$.Ailsun_Asbury->Adriaens_Whillock").value(0)
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
                        jsonPath("$[0].region").value("Argentina")
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
                        jsonPath("$[0].region").value("Campos Gerais")
                )
                .andExpect(
                        jsonPath("$[0].countUsers").value(1)
                );
    }
}
