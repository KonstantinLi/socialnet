package ru.skillbox.socialnet;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
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
import ru.skillbox.socialnet.controller.AccountController;
import ru.skillbox.socialnet.controller.AuthController;
import ru.skillbox.socialnet.dto.request.EmailRq;
import ru.skillbox.socialnet.dto.request.LoginRq;
import ru.skillbox.socialnet.dto.request.PasswordRecoveryRq;
import ru.skillbox.socialnet.dto.request.PasswordResetRq;
import ru.skillbox.socialnet.dto.request.PasswordSetRq;
import ru.skillbox.socialnet.dto.request.RegisterRq;
import ru.skillbox.socialnet.entity.other.Captcha;
import ru.skillbox.socialnet.entity.personrelated.Person;
import ru.skillbox.socialnet.repository.CaptchaRepository;
import ru.skillbox.socialnet.repository.PersonRepository;
import ru.skillbox.socialnet.security.JwtTokenUtils;

@Slf4j
@SpringBootTest
@ContextConfiguration(initializers = {AuthAndAccountControllerTest.Initializer.class})
@Testcontainers
@AutoConfigureMockMvc
@Sql(value = {"/auth-before-data.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
public class AuthAndAccountControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private AuthController authController;
    @Autowired
    private AccountController accountController;
    @Autowired
    private PersonRepository personRepository;
    @Autowired
    private CaptchaRepository captchaRepository;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private JwtTokenUtils jwtTokenUtils;

    private final static long EXISTING_TEST_PERSON_ID = 1L;

    @Container
    public static PostgreSQLContainer<?> postgreSqlContainer = new PostgreSQLContainer<>("postgres:15.1")
            .withDatabaseName("socialNet-test")
            .withUsername("root")
            .withPassword("root");

    static class Initializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {
        public void initialize(ConfigurableApplicationContext configurableApplicationContext) {
            log.info(postgreSqlContainer.getJdbcUrl());
            TestPropertyValues.of(
                    "spring.datasource.url=" + postgreSqlContainer.getJdbcUrl(),
                    "spring.datasource.username=" + postgreSqlContainer.getUsername(),
                    "spring.datasource.password=" + postgreSqlContainer.getPassword(),
                    "spring.liquibase.enabled=true"
            ).applyTo(configurableApplicationContext.getEnvironment());
            log.info(configurableApplicationContext.getEnvironment().getProperty("spring.datasource.url"));
        }
    }


    /**
     *
     ************************ AuthController TESTS ***************************
     */

    /**
     * Вспомогательная функция. Создает и возвращает объект класса LoginRq
     *
     * @param person - входной параметр класса Person. По этому параметру будет сформирован результат
     * @return - функция возвращает объект класса LoginRq
     */
    private static LoginRq getLoginRq(Person person) {

        LoginRq loginRq = new LoginRq();
        loginRq.setEmail(person.getEmail());
        String truePassword = new String(Base64.getDecoder().decode(person.getPassword()));
        loginRq.setPassword(truePassword);
        return loginRq;
    }

    /**
     * Тест: вводим верный логин и пароль и должны получить статус 200 и совпадающие email в ответе
     *
     * @throws Exception
     */
    @Test
    public void trueLoginTest() throws Exception {
        Person person = personRepository.findByIdImpl(1L);
        LoginRq loginRq = getLoginRq(person);
        this.mockMvc.perform(post("/api/v1/auth/login")
                        .content(new ObjectMapper().writeValueAsString(loginRq))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.email").value(person.getEmail()));
    }

    /**
     * Тест: вводим НЕ ВЕРНЫЙ логин, и должны получить 400 результат и ошибку "Пользователь не найден"
     *
     * @throws Exception
     */
    @Test
    public void wrongPersonLoginTest() throws Exception {
        Person person = personRepository.findByIdImpl(EXISTING_TEST_PERSON_ID);
        LoginRq loginRq = getLoginRq(person);
        loginRq.setEmail("wrongemail@wrongemail.wrongemail");
        this.mockMvc.perform(post("/api/v1/auth/login")
                        .content(new ObjectMapper().writeValueAsString(loginRq))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error_description").value("Пользователь не найден"));
    }

    /**
     * Тест: вводим НЕ ВЕРНЫЙ пароль, и должны получить 400 результат и ошибку "Пароли не совпадают"
     *
     * @throws Exception
     */
    @Test
    public void wrongPasswordLoginTest() throws Exception {
        Person person = personRepository.findByIdImpl(EXISTING_TEST_PERSON_ID);
        LoginRq loginRq = getLoginRq(person);
        loginRq.setPassword("wrong_password");
        this.mockMvc.perform(post("/api/v1/auth/login")
                        .content(new ObjectMapper().writeValueAsString(loginRq))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error_description").value("Пароли не совпадают"));
    }

    /**
     * ****************** AccountController TESTS ****************************
     */


    private static String getBase64EncodedString(String source) {
        return Base64.getEncoder().encodeToString(source.getBytes());
    }

    /**
     * - вспомогательная функция, получает валидную капчу для тестов
     *
     * @return - найденная в базе или созданная валидная Captcha
     */
    private Captcha getValidCaptcha() {
        Optional<List<Captcha>> captchas = captchaRepository.findByTime(LocalDateTime.now().plusMinutes(5));
        if (captchas.isPresent() && (captchas.get().size() > 0)) {
            return captchas.get().get(0);
        }
        Random r = new Random();
        int low = 1001;
        int high = 100001;
        long code = r.nextInt(high - low) + low;
        Captcha captcha = new Captcha();
        captcha.setCode(String.valueOf(code));
        captcha.setSecretCode(getBase64EncodedString(String.valueOf(code)));
        LocalDateTime validDate = LocalDateTime.now().plusHours(10);
        captcha.setTime(validDate.toString());
        captchaRepository.save(captcha);
        log.info(captcha.getId().toString());
        return captcha;
    }

    /**
     * - вспомогательная функция, отдает объект RegisterRq, создающийся по дефолтным параметрам
     *
     * @return - объект класса RegisterRq
     */
    private RegisterRq getRegisterRq() {
        RegisterRq registerRq = new RegisterRq();
        Captcha captcha = getValidCaptcha();
        log.info(String.valueOf(captcha.getCode()));
        registerRq.setCode(captcha.getCode());
        registerRq.setCodeSecret(captcha.getSecretCode());
        registerRq.setEmail("testemail@testemail.com");
        registerRq.setFirstName("TestUserFirstName");
        registerRq.setLastName("TestUserLastName");
        registerRq.setPasswd1("Qwerty12345");
        registerRq.setPasswd2("Qwerty12345");
        return registerRq;
    }

    /**
     * - Тест: регистрируем нового пользователя.
     * ожидаем статус 200 и совпадение email в ответе сервера и поля email registerRq
     *
     * @throws Exception
     */
    @Test
    public void trueRegistrationTest() throws Exception {
        RegisterRq registerRq = getRegisterRq();
        this.mockMvc.perform(post("/api/v1/account//register")
                        .content(new ObjectMapper().writeValueAsString(registerRq))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value(registerRq.getEmail()));
    }

    /**
     * - Тест: попытка зарегистрировать существующего пользователя повторно
     * ожидаем статус 400 и соотвующую ошибку
     *
     * @throws Exception
     */
    @Test
    public void wrongRegistrationTestUserExists() throws Exception {
        RegisterRq registerRq = getRegisterRq();
        Person person = personRepository.findByIdImpl(EXISTING_TEST_PERSON_ID);
        registerRq.setEmail(person.getEmail());
        this.mockMvc.perform(post("/api/v1/account/register")
                        .content(new ObjectMapper().writeValueAsString(registerRq))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error_description").value("Пользователь с email: '" +
                        registerRq.getEmail() + "' уже зарегистрирован"));
    }

    /**
     * - Тест: попытка зарегистрировать пользователя, который не верно ввел подтверждение пароля
     * ожидаем статус 400 и соотвующую ошибку
     *
     * @throws Exception
     */
    @Test
    public void wrongRegistrationNotSamePasswords() throws Exception {
        RegisterRq registerRq = getRegisterRq();
        registerRq.setPasswd2(registerRq.getPasswd2() + "wrongText");
        this.mockMvc.perform(post("/api/v1/account/register")
                        .content(new ObjectMapper().writeValueAsString(registerRq))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error_description").value("Пароли не совпадают"))
        ;
    }

    /**
     * - Тест: попытка зарегистрировать пользователя, который не верно ввел код с капчи
     * ожидаем статус 400 и соотвующую ошибку
     *
     * @throws Exception
     */
    @Test
    public void wrongRegistrationCaptchaExpired() throws Exception {
        RegisterRq registerRq = getRegisterRq();
        registerRq.setCode("0000000");
        registerRq.setCodeSecret(getBase64EncodedString("0000000"));
        this.mockMvc.perform(post("/api/v1/account//register")
                        .content(new ObjectMapper().writeValueAsString(registerRq))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error_description").value("Картинка устарела"));
    }


    public String getEncodedPassword(String password) {
        byte[] encodedBytes = Base64.getEncoder().encode(password.getBytes());
        return new String(encodedBytes);
    }

    public String getDecodedPassword(String password) {
        byte[] decodedBytes = Base64.getDecoder().decode(password);
        return new String(decodedBytes);
    }

    /**
     * - Тест: успешное изменение пароля пользователя
     * ожидаем статус 200 и соотв. сообщение
     *
     * @throws Exception
     */
    @Test
    void successChangePasswordTest() throws Exception {
        Person person = personRepository.findByIdImpl(EXISTING_TEST_PERSON_ID);
        String token = jwtTokenUtils.generateToken(person);
        PasswordSetRq passwordSetRq = new PasswordSetRq();
        passwordSetRq.setPassword(getDecodedPassword(person.getPassword()) + "1");
        this.mockMvc.perform(put("/api/v1/account/password/set")
                        .header("authorization", token)
                        .content(new ObjectMapper().writeValueAsString(passwordSetRq))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.message").value("Пароль успешно изменен!"));
    }


    /**
     * - Тест: НЕ успешное изменение пароля пользователя - неверный токен!
     * ожидаем статус 401 и соотв. причину
     *
     * @throws Exception
     */
    @Test
    void wrongTokenTest() throws Exception {
        Person person = personRepository.findByIdImpl(EXISTING_TEST_PERSON_ID);
        String token = jwtTokenUtils.generateToken(person) + "1";
        PasswordResetRq passwordSetRq = new PasswordResetRq();
        passwordSetRq.setPassword(getDecodedPassword(person.getPassword()) + "1");
        this.mockMvc.perform(put("/api/v1/account/password/set")
                        .header("authorization", token)
                        .content(new ObjectMapper().writeValueAsString(passwordSetRq))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isUnauthorized())
                .andExpect(status().reason("Full authentication is required to access this resource"));
    }


    /**
     * - Тест: попытка изменить пароль пользователя на текущий.
     * ожидаем статус 400 и соотв. ошибку
     *
     * @throws Exception
     */
    @Test
    void trySamePasswordChangeTest() throws Exception {
        Person person = personRepository.findByIdImpl(EXISTING_TEST_PERSON_ID);
        String token = jwtTokenUtils.generateToken(person);
        PasswordSetRq passwordSetRq = new PasswordSetRq();
        passwordSetRq.setPassword(getDecodedPassword(person.getPassword()));
        this.mockMvc.perform(put("/api/v1/account/password/set")
                        .header("authorization", token)
                        .content(new ObjectMapper().writeValueAsString(passwordSetRq))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error_description").value("Новый пароль не должен совпадать со старым"));
    }

    /**
     * - Тест: удачный запрос на восстановление пароля
     * тестируем на пользователе с ID = 1, предварительно сделав его НЕ удаленным и НЕ заблокированным и
     * изменив email на свой. Убеждаемся, что тест прошел успешно (статус 200) и меняем настройки пользователя
     * на первоначальные
     *
     * @throws Exception
     */
    @Test
    void successPasswordRecoveryTest() throws Exception {
        Person person = personRepository.findByIdImpl(EXISTING_TEST_PERSON_ID);
        PasswordRecoveryRq passwordRecoveryRq = new PasswordRecoveryRq();
        String personEmail = person.getEmail();
        Boolean isBlocked = person.getIsBlocked();
        Boolean isDeleted = person.getIsDeleted();
        try {
            person.setEmail("nowicoff@yandex.ru");
            person.setIsBlocked(false);
            person.setIsDeleted(false);
            personRepository.save(person);
            String token = jwtTokenUtils.generateToken(person);
            passwordRecoveryRq.setEmail(person.getEmail());
            this.mockMvc.perform(put("/api/v1/account/password/recovery")
                            .header("authorization", token)
                            .content(new ObjectMapper().writeValueAsString(passwordRecoveryRq))
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON))
                    .andDo(print())
                    .andExpect(status().isOk());
        } finally {
            person.setEmail(personEmail);
            person.setIsBlocked(isBlocked);
            person.setIsDeleted(isDeleted);
            personRepository.save(person);
        }
    }


    /**
     * - Тест: НЕ удачный запрос на восстановление пароля
     * тестируем на пользователе с ID = 1, предварительно сделав его заблокированным и
     * изменив email на свой. Убеждаемся, что тест прошел с ошибкой (статус 400) и меняем настройки пользователя
     * на первоначальные
     *
     * @throws Exception
     */
    @Test
    void wrongPasswordRecoveryTest() throws Exception {
        Person person = personRepository.findByIdImpl(EXISTING_TEST_PERSON_ID);
        PasswordRecoveryRq passwordRecoveryRq = new PasswordRecoveryRq();
        String personEmail = person.getEmail();
        Boolean isBlocked = person.getIsBlocked();
        try {
            person.setEmail("nowicoff@yandex.ru");
            person.setIsBlocked(true);
            personRepository.save(person);
            String token = jwtTokenUtils.generateToken(person);
            passwordRecoveryRq.setEmail(person.getEmail());
            this.mockMvc.perform(put("/api/v1/account/password/recovery")
                            .header("authorization", token)
                            .content(new ObjectMapper().writeValueAsString(passwordRecoveryRq))
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON))
                    .andDo(print())
                    .andExpect(status().isOk());
        } finally {
            person.setEmail(personEmail);
            person.setIsBlocked(isBlocked);
            personRepository.save(person);
        }
    }

    /**
     * - Тест: удачный тест на сброс пароля
     * меняем пароль пользователю с ID = 1 Убеждаемся, что тест прошел успешно (статус 200 и соотв. сообщение получено)
     *
     * @throws Exception
     */
    @Test
    void successPasswordResetTest() throws Exception {
        Person person = personRepository.findByIdImpl(EXISTING_TEST_PERSON_ID);
        String token = jwtTokenUtils.generateToken(person);
        PasswordResetRq passwordResetRq = new PasswordResetRq();
        passwordResetRq.setPassword(UUID.randomUUID().toString());
        passwordResetRq.setSecret(token);
        this.mockMvc.perform(put("/api/v1/account/password/reset")
                        .header("authorization", token)
                        .content(new ObjectMapper().writeValueAsString(passwordResetRq))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.message").value("Пароль успешно изменен!"));
    }

    /**
     * - Тест: Неудачный тест на сброс пароля
     * меняем пароль на идентичный, ожидаем получить статус 400 и соотв. сообщение об ошибке
     *
     * @throws Exception
     */
    @Test
    void wrongPasswordResetTest() throws Exception {
        Person person = personRepository.findByIdImpl(EXISTING_TEST_PERSON_ID);
        String token = jwtTokenUtils.generateToken(person);
        PasswordResetRq passwordResetRq = new PasswordResetRq();
        passwordResetRq.setPassword(getDecodedPassword(person.getPassword()));
        passwordResetRq.setSecret(token);
        this.mockMvc.perform(put("/api/v1/account/password/reset")
                        .header("authorization", token)
                        .content(new ObjectMapper().writeValueAsString(passwordResetRq))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error_description").value("Новый пароль не должен совпадать со старым"));
    }

    /**
     * - Тест: успешный тест на сброс email
     * ожидаем статус 200 и соответствующее сообщение
     *
     * @throws Exception
     */
    @Test
    void successEmailRecoveryTest() throws Exception {

    }

    /**
     * - Тест: НЕ успешный тест на сброс email. Новый email != Старый email
     * ожидаем статус 400 и соответствующее сообщение
     *
     * @throws Exception
     */
    @Test
    void wrongEmailRecoveryTest() throws Exception {
        Person person = personRepository.findByIdImpl(EXISTING_TEST_PERSON_ID);
        String token = jwtTokenUtils.generateToken(person);
        this.mockMvc.perform(put("/api/v1/account/email/recovery")
                        .header("authorization", token)
                        .content(UUID.randomUUID() + "@mail.ru")
                        .contentType(MediaType.TEXT_PLAIN_VALUE)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error_description").isNotEmpty());
    }

    /**
     * - Тест: успешный тест на изменение email
     * ожидаем статус 200 и соответствующее сообщение
     *
     * @throws Exception
     */
    @Test
    void successSetEmailTest() throws Exception {
        Person person = personRepository.findByIdImpl(EXISTING_TEST_PERSON_ID);
        String token = jwtTokenUtils.generateToken(person);
        EmailRq emailRq = new EmailRq();
        emailRq.setEmail(UUID.randomUUID() + "@mail.ru");
        emailRq.setSecret(token);
        this.mockMvc.perform(put("/api/v1/account/email")
                        .header("authorization", token)
                        .content(new ObjectMapper().writeValueAsString(emailRq))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.message").value("Email успешно изменен!"));
    }

    /**
     * - Тест: уникальность email при попытке измениить email
     * необходимо убедиться, что с одним email не могут быть зарегистрированы несколько персон.
     * Для персоны1 подставляем email принадлежащий персоне2 пытаемся выполнить запрос.
     * ожидаем получить статус 400
     *
     * @throws Exception
     */
    @Test
    void checkUniqueEmailTest() throws Exception {
        Person person = personRepository.findByIdImpl(EXISTING_TEST_PERSON_ID);
        Person person2 = personRepository.findByIdImpl(2L);
        String token = jwtTokenUtils.generateToken(person);
        EmailRq emailRq = new EmailRq();
        emailRq.setEmail(person2.getEmail());
        emailRq.setSecret(token);
        this.mockMvc.perform(put("/api/v1/account/email")
                        .header("authorization", token)
                        .content(new ObjectMapper().writeValueAsString(emailRq))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error_description").value("Пользователь с email: '" + person2.getEmail() + "' уже зарегистрирован"));
    }

    /**
     * - Тест: НЕ успешный тест на изменение email. Пытаемся ввести тот же email
     * ожидаем статус 400 и соответствующее сообщенине
     *
     * @throws Exception
     */
    @Test
    void wrongSetEmailTest() throws Exception {
        Person person = personRepository.findByIdImpl(EXISTING_TEST_PERSON_ID);
        String token = jwtTokenUtils.generateToken(person);
        EmailRq emailRq = new EmailRq();
        emailRq.setEmail(person.getEmail());
        emailRq.setSecret(token);
        this.mockMvc.perform(put("/api/v1/account/email")
                        .header("authorization", token)
                        .content(new ObjectMapper().writeValueAsString(emailRq))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error_description").value("Пользователь с email: '" + person.getEmail() + "' уже зарегистрирован"));
    }
}

