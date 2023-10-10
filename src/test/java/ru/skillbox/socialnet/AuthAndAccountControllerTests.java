package ru.skillbox.socialnet;

import org.junit.Test;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
//import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import ru.skillbox.socialnet.controller.AccountController;
import ru.skillbox.socialnet.controller.AuthController;
import ru.skillbox.socialnet.dto.request.LoginRq;
import ru.skillbox.socialnet.dto.request.RegisterRq;
import ru.skillbox.socialnet.entity.other.Captcha;
import ru.skillbox.socialnet.entity.personrelated.Person;
import ru.skillbox.socialnet.repository.CaptchaRepository;
import ru.skillbox.socialnet.repository.PersonRepository;

import java.time.LocalDateTime;
import java.util.Base64;
import java.util.List;
import java.util.Optional;
import java.util.Random;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource("/application-test.yml")
@Sql(value = {"/post-before-data.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)

public class AuthAndAccountControllerTests {
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

    private final static long EXISTING_TEST_PERSON_ID = 1L;

    /**
     *
     ************************ AuthController TESTS ***************************
     */

    /**
     *  Вспомогательная функция. Создает и возвращает объект класса LoginRq
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
     *  Тест: вводим НЕ ВЕРНЫЙ пароль, и должны получить 400 результат и ошибку "Пароли не совпадают"
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
     *
     ******************* AccountController TESTS ****************************
     *
     */


    private static String getBase64EncodedString(String source) {
        return Base64.getEncoder().encodeToString(source.getBytes());
    }

    /**
     * - вспомогательная функция, получает валидную капчу для тестов
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
     *  - вспомогательная функция, отдает объект RegisterRq, создающийся по дефолтным параметрам
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
     *  - Тест: регистрируем нового пользователя.
     *  ожидаем статус 200 и совпадение email в ответе сервера и поля email registerRq
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
     *  ожидаем статус 400 и соотвующую ошибку
     * @throws Exception
     */
    @Test
    public void wrongRegistrationTestUserExists() throws Exception {
        RegisterRq registerRq = getRegisterRq();
        Person person = personRepository.findByIdImpl(EXISTING_TEST_PERSON_ID);
        registerRq.setEmail(person.getEmail());
        this.mockMvc.perform(post("/api/v1/account//register")
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
     *  ожидаем статус 400 и соотвующую ошибку
     * @throws Exception
     */
    @Test
    public void wrongRegistrationNotSamePasswords() throws Exception {
        RegisterRq registerRq = getRegisterRq();
        registerRq.setPasswd2(registerRq.getPasswd2()+"wrongText");
        this.mockMvc.perform(post("/api/v1/account//register")
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
     *  ожидаем статус 400 и соотвующую ошибку
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
                .andExpect(jsonPath("$.error_description").value("Картинка устарела"))
        ;
    }

}

