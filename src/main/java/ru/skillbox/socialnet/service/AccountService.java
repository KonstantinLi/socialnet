package ru.skillbox.socialnet.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.skillbox.socialnet.dto.request.RegisterRq;
import ru.skillbox.socialnet.dto.response.ComplexRs;
import ru.skillbox.socialnet.dto.response.RegisterRs;
import ru.skillbox.socialnet.entity.enums.MessagePermission;
import ru.skillbox.socialnet.entity.other.Captcha;
import ru.skillbox.socialnet.entity.personrelated.Person;
import ru.skillbox.socialnet.entity.personrelated.PersonSettings;
import ru.skillbox.socialnet.exception.auth.AuthException;
import ru.skillbox.socialnet.repository.CaptchaRepository;
import ru.skillbox.socialnet.repository.PersonRepository;
import ru.skillbox.socialnet.repository.PersonSettingsRepository;

import java.util.Base64;
import java.util.Date;

@Service
@RequiredArgsConstructor
public class AccountService {
    private final PersonRepository personRepository;
    private final PersonSettingsRepository personSettingsRepository;
    private final CaptchaRepository captchaRepository;

    @Value("${aws.default-photo-url}")
    private String defaultPhotoUrl;

    public RegisterRs<ComplexRs> registration(RegisterRq registerRq) {

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

    private Person addPerson(RegisterRq registrationInfo) {

        Person person = new Person();
        person.setEmail(registrationInfo.getEmail());
        person.setPassword(getEncodedPassword(registrationInfo.getPasswd1()));
        person.setFirstName(registrationInfo.getFirstName());
        person.setLastName(registrationInfo.getLastName());
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


