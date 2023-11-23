package ru.skillbox.socialnet;

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
import ru.skillbox.socialnet.entity.enums.FriendShipStatus;
import ru.skillbox.socialnet.entity.personrelated.FriendShip;
import ru.skillbox.socialnet.entity.personrelated.Person;
import ru.skillbox.socialnet.repository.FriendShipRepository;
import ru.skillbox.socialnet.repository.PersonRepository;
import ru.skillbox.socialnet.security.JwtTokenUtils;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ContextConfiguration(initializers = {FriendsControllerTest.Initializer.class})
@Testcontainers
@AutoConfigureMockMvc
@Sql(value = {"/FriendsController-before-test.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(value = {"/FriendsController-after-test.sql"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
class FriendsControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private PersonRepository personRepository;
    @Autowired
    private FriendShipRepository friendShipRepository;
    @Autowired
    private JwtTokenUtils jwtTokenUtils;

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

    String getToken(Long id) {
        Optional<Person> optPerson = personRepository.findById(id);
        if (optPerson.isEmpty()) {
            throw new RuntimeException("не удалось найти Person с идентификатором " + id);
        }
        return jwtTokenUtils.generateToken(optPerson.get());
    }

    @Test
    void unauthorizedTest() throws Exception {
        this.mockMvc.perform(get("/api/v1/friends"))
                .andDo(print())
                .andExpect(status().is4xxClientError())
                .andExpect(status().isUnauthorized());
    }

    @Test
    void sendFriendshipRequestTest() throws Exception {
        this.mockMvc.perform(post("/api/v1/friends/2").header("authorization", getToken(1L)))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    void sendFriendshipRequestFailTest() throws Exception {
        this.mockMvc.perform(post("/api/v1/friends/10").header("authorization", getToken(1L)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("PersonNotFoundException"))
                .andExpect(jsonPath("$.error_description").value("Запись о профиле не найдена"));
    }

    @Test
    void deleteFriendByIdTest() throws Exception {
        this.mockMvc.perform(delete("/api/v1/friends/3").header("authorization", getToken(1L)))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    void deleteFriendByIdFailTest() throws Exception {
        this.mockMvc.perform(delete("/api/v1/friends/2").header("authorization", getToken(1L)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("FriendShipNotFoundException"))
                .andExpect(jsonPath("$.error_description").value("запись о дружбе не найдена"));
    }

    @Test
    void deleteFriendByIdFailProfileTest() throws Exception {
        this.mockMvc.perform(delete("/api/v1/friends/10").header("authorization", getToken(1L)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("PersonNotFoundException"))
                .andExpect(jsonPath("$.error_description").value("Запись о профиле не найдена"));
    }

    @Test
    void addFriendByIdTest() throws Exception {
        this.mockMvc.perform(post("/api/v1/friends/request/4").header("authorization", getToken(1L)))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    void addFriendByIdFailRequestTest() throws Exception {
        this.mockMvc.perform(post("/api/v1/friends/request/3").header("authorization", getToken(2L)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("FriendShipNotFoundException"))
                .andExpect(jsonPath("$.error_description").value("исходящий запрос на дружбу не найден"));
    }

    @Test
    void addFriendByIdFailProfileTest() throws Exception {
        this.mockMvc.perform(post("/api/v1/friends/request/10").header("authorization", getToken(1L)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("PersonNotFoundException"))
                .andExpect(jsonPath("$.error_description").value("Запись о профиле не найдена"));
    }

    @Test
    void declineFriendshipRequestByIdTest() throws Exception {
        this.mockMvc.perform(delete("/api/v1/friends/request/5").header("authorization", getToken(1L)))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    void declineFriendshipRequestByIdFailRequestTest() throws Exception {
        this.mockMvc.perform(delete("/api/v1/friends/request/6").header("authorization", getToken(1L)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("FriendShipNotFoundException"))
                .andExpect(jsonPath("$.error_description").value("входящий запрос на дружбу не найден"));
    }

    @Test
    void declineFriendshipRequestByIdFailProfileTest() throws Exception {
        this.mockMvc.perform(delete("/api/v1/friends/request/10").header("authorization", getToken(1L)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("PersonNotFoundException"))
                .andExpect(jsonPath("$.error_description").value("Запись о профиле не найдена"));
    }

    @Test
    void blockOrUnblockUserByUserTest() throws Exception {
        String token = getToken(1L);
        this.mockMvc.perform(post("/api/v1/friends/block_unblock/2").header("authorization", token))
                .andDo(print())
                .andExpect(status().isOk());
        Optional<FriendShip> optFriendShip = friendShipRepository
                .getFriendShipByIdsAndStatus(1L, 2L, "BLOCKED");
        assertTrue(optFriendShip.isPresent());
        this.mockMvc.perform(post("/api/v1/friends/block_unblock/2").header("authorization", token))
                .andDo(print())
                .andExpect(status().isOk());
        Optional<FriendShipStatus> optFriendShipStatus = friendShipRepository.getFriendShipStatusBetweenTwoPersons(1L, 2L);
        assertTrue(optFriendShipStatus.isEmpty());
    }

    @Test
    void blockOrUnblockUserByUserFailProfileTest() throws Exception {
        String token = getToken(1L);
        this.mockMvc.perform(post("/api/v1/friends/block_unblock/10").header("authorization", token))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("PersonNotFoundException"))
                .andExpect(jsonPath("$.error_description").value("Запись о профиле не найдена"));
    }

    @Test
    void getFriendsOfCurrentUserTest() throws Exception {
        this.mockMvc.perform(get("/api/v1/friends").header("authorization", getToken(1L)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].birth_date").value("2008-02-24T00:00:00"))
                .andExpect(jsonPath("$.data[0].first_name").value("Ailsun"))
                .andExpect(jsonPath("$.data[0].last_name").value("Asbury"));
    }

    @Test
    void getPotentialFriendsOfCurrentUserTest() throws Exception {
        this.mockMvc.perform(get("/api/v1/friends/request").header("authorization", getToken(1L)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].first_name").value("Thaxter"))
                .andExpect(jsonPath("$.data[1].last_name").value("Morena"));
    }

    @Test
    void getRecommendationFriendsTest() throws Exception {
        this.mockMvc.perform(get("/api/v1/friends/recommendations").header("authorization", getToken(1L)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].id").value("6"));
    }

    @Test
    void getOutgoingRequestsByUserTest() throws Exception {
        this.mockMvc.perform(get("/api/v1/friends/outgoing_requests").header("authorization", getToken(1L)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.total").value(1));
    }
}
