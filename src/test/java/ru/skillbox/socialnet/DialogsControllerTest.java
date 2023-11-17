package ru.skillbox.socialnet;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
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
import ru.skillbox.socialnet.dto.request.DialogUserShortListRq;
import ru.skillbox.socialnet.entity.personrelated.Person;
import ru.skillbox.socialnet.repository.DialogRepository;
import ru.skillbox.socialnet.repository.MessageRepository;
import ru.skillbox.socialnet.repository.PersonRepository;
import ru.skillbox.socialnet.security.JwtTokenUtils;

@ContextConfiguration(initializers = {DialogsControllerTest.Initializer.class})
@Testcontainers
@SpringBootTest
@AutoConfigureMockMvc
@Sql(value = {"/dialogs-before-data.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
class DialogsControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private PersonRepository personRepository;
    @Autowired
    private JwtTokenUtils jwtTokenUtils;

    @Autowired
    private DialogRepository dialogRepository;

    @Autowired
    private MessageRepository messageRepository;

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

    private String getToken(Long userId) {

        if (token != null) {
            return token;
        }

        Person person = personRepository.findById(userId).get();
        String token = jwtTokenUtils.generateToken(person);
        this.token = token;

        return token;
    }

    @Test
    void setReadDialog() throws Exception{
        String token = getToken(1L);

        var countUnread = messageRepository.countUnreadMessagesByDialogId(1L);
        assertEquals(1, countUnread);
        this.mockMvc.perform(put("/api/v1/dialogs/1/read")
                .header("authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
            )
            .andDo(print())
            .andExpect(status().isOk());
        var countUnreadNew = messageRepository.countUnreadMessagesByDialogId(1L);
        assertEquals(0, countUnreadNew);
    }

    @Test
    void getDialogs() throws Exception {
        String token = getToken(2L);

        this.mockMvc.perform(get("/api/v1/dialogs")
                .header("authorization", token))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data.size()").value(2));
    }

    @Test
    void startDialog() throws Exception {
        String token = getToken(1L);
        DialogUserShortListRq dialogUserShortListRq = new DialogUserShortListRq();
        dialogUserShortListRq.setUserId(1L);
        dialogUserShortListRq.setUserIds(List.of(3L));

        this.mockMvc.perform(post("/api/v1/dialogs")
                .header("authorization", token)
                .content(new ObjectMapper().writeValueAsString(dialogUserShortListRq))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(status().isOk());
        assertEquals(3, dialogRepository.findAll().size());
        var dialog = dialogRepository.findById(3L).get();
        assertEquals(1L, dialog.getFirstPerson().getId());
        assertEquals(3L, dialog.getSecondPerson().getId());
    }

    @Test
    void getUnreadedDialogs() throws Exception {
        String token = getToken(1L);

        this.mockMvc.perform(get("/api/v1/dialogs/unreaded")
                .header("authorization", token)
            )
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data.count").value(1));

    }

    @Test
    void getMessageFromDialog() throws Exception {
        String token = getToken(1L);

        this.mockMvc.perform(get("/api/v1/dialogs/1/messages")
                .header("authorization", token)
                .param("offset", "0")
                .param("perPage", "20")
            )
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data.size()").value(3));
    }
}