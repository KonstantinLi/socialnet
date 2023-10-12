package ru.skillbox.socialnet.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import ru.skillbox.socialnet.annotation.Debug;
import ru.skillbox.socialnet.dto.EmailContentManager;
import ru.skillbox.socialnet.dto.EmailHandler;
import ru.skillbox.socialnet.dto.request.*;
import ru.skillbox.socialnet.dto.response.ComplexRs;
import ru.skillbox.socialnet.dto.response.RegisterRs;
import ru.skillbox.socialnet.entity.enums.MessagePermission;
import ru.skillbox.socialnet.entity.other.Captcha;
import ru.skillbox.socialnet.entity.personrelated.Person;
import ru.skillbox.socialnet.entity.personrelated.PersonSettings;
import ru.skillbox.socialnet.exception.*;
import ru.skillbox.socialnet.repository.CaptchaRepository;
import ru.skillbox.socialnet.repository.PersonRepository;
import ru.skillbox.socialnet.repository.PersonSettingsRepository;
import ru.skillbox.socialnet.security.JwtTokenUtils;

import java.util.Base64;
import java.util.Date;

@Slf4j
@Service
@RequiredArgsConstructor
@Debug
public class AccountService {
    private final PersonRepository personRepository;
    private final PersonSettingsRepository personSettingsRepository;
    private final CaptchaRepository captchaRepository;
    private final JwtTokenUtils jwtTokenUtils;
    private final EmailHandler emailHandler;
    private final EmailContentManager contentManager;


    @Value("${aws.default-photo-url}")
    private String defaultPhotoUrl;
    
    public RegisterRs<ComplexRs> registration(RegisterRq registerRq) throws BadRequestException {

        if (!registerRq.getPasswd1().equals(registerRq.getPasswd2())) {
            throw new AuthException("Пароли не совпадают");
        }
        Captcha captcha = captchaRepository.findBySecretCode(registerRq.getCodeSecret()).orElseThrow(
                () -> new AuthException("Картинка устарела"));
        if (!registerRq.getCode().equals(captcha.getCode())) {
            throw new AuthException("Введенный код не совпадает с кодом картинки");
        }
        if (personRepository.findByEmail(registerRq.getEmail()).isEmpty()) {
            Person person = addPerson(registerRq);
            person.setMessagePermissions(MessagePermission.ALL);
            person.setOnlineStatus(false);
            personRepository.save(person);
        } else {
            throw new AuthException("Пользователь с email: '" + registerRq.getEmail() +
                    "' уже зарегистрирован");
        }
        RegisterRs<ComplexRs> response = new RegisterRs<>();
        ComplexRs complexRs = new ComplexRs();
        response.setData(complexRs);
        response.setEmail(registerRq.getEmail());
        response.setTimestamp(new Date().getTime());

        return response;
    }

    public RegisterRs<ComplexRs> setPassword(PasswordSetRq passwordSetRq) throws BadRequestException {
        String userId = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName().split(",", 2)[0];

        Person person = personRepository.findById(Long.parseLong(userId)).orElseThrow(
                () -> new PersonNotFoundException("Пользователь id: " + userId + " не найден"));

        if (person.getPassword().equals(getEncodedPassword(passwordSetRq.getPassword()))) {
            throw new PasswordIsNotChangedException("Новый пароль не должен совпадать со старым");
        }

        person.setPassword(getEncodedPassword(passwordSetRq.getPassword()));

        personRepository.save(person);

        RegisterRs<ComplexRs> response = new RegisterRs<>();
        ComplexRs complexRs = new ComplexRs();
        complexRs.setMessage("Пароль успешно изменен!");
        response.setData(complexRs);

        return response;
    }

    public void passwordRecovery(PasswordRecoveryRq passwordRecoveryRq) {

        String email = passwordRecoveryRq.getEmail();
        Person person = personRepository.findByEmail(email).orElseThrow(
                () -> new PersonNotFoundException("Пользователь " + email + " не найден"));
        if (Boolean.TRUE.equals(person.getIsDeleted())) {
            throw new PersonIsBlockedException("Пользователь " + email + " удален");
        } else if (Boolean.TRUE.equals(person.getIsBlocked())) {
            throw new PersonNotFoundException("Пользователь " + email + " не найден");
        }

        String token = jwtTokenUtils.generateToken(person);

        String emailContent = contentManager.getRecoveryEmailContent(
                EmailContentManager.EmailType.PASSWORD_RECOVERY, token);

        emailHandler.sendEmail(email, "Password recovery", emailContent);
    }

    public RegisterRs<ComplexRs> resetPassword(PasswordRq passwordSetRq) {

        String token = passwordSetRq.getSecret();
        Long userId = jwtTokenUtils.getId(token);

        if (userId == null) {
            throw new TokenParseException("Ошибка при сбросе пароля: неверный токен!");
        }

        Person person = personRepository.findById(userId).orElseThrow(
                () -> new PersonNotFoundException("Пользователь не найден"));

        String currentPassword = person.getPassword();
        String newPassword = getEncodedPassword(passwordSetRq.getPassword());

        if (currentPassword.equals(newPassword)) {
            throw new PasswordIsNotChangedException("Новый пароль не должен совпадать со старым");
        }

        person.setPassword(newPassword);
        personRepository.save(person);

        RegisterRs<ComplexRs> response = new RegisterRs<>();
        ComplexRs complexRs = new ComplexRs();
        complexRs.setMessage("Пароль успешно изменен!");
        response.setData(complexRs);
        response.setEmail(person.getEmail());

        return response;
    }

    public void emailRecovery(String token, String email) {

        Long userId = jwtTokenUtils.getId(token);

        if (userId == null) {
            throw new TokenParseException("Ошибка при сбросе почты: неверный токен!");
        }

        Person person = personRepository.findById(userId).orElseThrow(
                () -> new PersonNotFoundException("Пользователь не найден"));

        if (!person.getEmail().equals(email)) {
            throw new BadRequestException("Ошибка идетификации пользователя: email не совпадает!");
        }

        String emailContent = contentManager.getRecoveryEmailContent(
                EmailContentManager.EmailType.EMAIL_RECOVERY, token);

        emailHandler.sendEmail(email, "Email recovery", emailContent);
    }

    public RegisterRs<ComplexRs> setEmail(EmailRq emailRq) {

        Long userId = jwtTokenUtils.getId(emailRq.getSecret());

        Person person = personRepository.findById(userId).orElseThrow(
                () -> new PersonNotFoundException("Пользователь id: " + userId + " не найден"));

        if (person.getEmail().equals(emailRq.getEmail())) {
            throw new EmailIsNotChangedException("Новый email не должен совпадать со старым");
        }

        person.setEmail(emailRq.getEmail());

        personRepository.save(person);

        RegisterRs<ComplexRs> response = new RegisterRs<>();
        ComplexRs complexRs = new ComplexRs();
        complexRs.setMessage("Email успешно изменен!");
        response.setData(complexRs);

        return response;
    }

    private Person addPerson(RegisterRq registrationInfo) {

        Person person = new Person();
        person.setEmail(registrationInfo.getEmail());
        person.setPassword(getEncodedPassword(registrationInfo.getPasswd1()));
        person.setFirstName(registrationInfo.getFirstName());
        person.setLastName(registrationInfo.getLastName());
        person.setMessagePermissions(MessagePermission.ALL);
        person.setOnlineStatus(true);
        PersonSettings personSettings = new PersonSettings();
        personSettingsRepository.save(personSettings);
        person.setPersonSettings(personSettings);
        person.setMessagePermissions(MessagePermission.ALL);
        person.setOnlineStatus(true);
        person.setPhoto(defaultPhotoUrl);

        return person;
    }

    public String getEncodedPassword(String password) {
        byte[] encodedBytes = Base64.getEncoder().encode(password.getBytes());
        return new String(encodedBytes);
    }

    public String getDecodedPassword(String password) {
        byte[] decodedBytes = Base64.getDecoder().decode(password);
        return new String(decodedBytes);
    }
}


