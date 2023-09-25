package ru.skillbox.socialnet.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.skillbox.socialnet.dto.response.ComplexRs;
import ru.skillbox.socialnet.dto.response.RegisterRs;
import ru.skillbox.socialnet.entity.Person;
import ru.skillbox.socialnet.entity.PersonSettings;
import ru.skillbox.socialnet.entity.other.Captcha;
import ru.skillbox.socialnet.errs.BadRequestException;
import ru.skillbox.socialnet.exception.ExceptionBadRq;
import ru.skillbox.socialnet.model.RegisterRq;
import ru.skillbox.socialnet.repository.CaptchaRepository;
import ru.skillbox.socialnet.repository.PersonRepository;
import ru.skillbox.socialnet.repository.PersonSettingsRepository;
import ru.skillbox.socialnet.util.ValidationUtilsRq;

import java.util.Base64;
import java.util.Date;

@Service
@RequiredArgsConstructor
public class AccountService {
    public final PersonRepository personRepository;
    public final ValidationUtilsRq validationUtils;
    public final PersonSettingsRepository personSettingsRepository;
    public final CaptchaRepository captchaRepository;

    public RegisterRs<ComplexRs> registration(RegisterRq registerRq) throws ExceptionBadRq, BadRequestException {
        validationUtils.validationRegPassword(registerRq.getPasswd1(), registerRq.getPasswd2());
        Captcha captcha = captchaRepository.findBySecretСode(registerRq.getCodeSecret()).orElseThrow(
                () -> new BadRequestException(""));
        validationUtils.validationCode(registerRq.getCode(), captcha.getCode());
        if (personRepository.findByEmail(registerRq.getEmail()).isEmpty()) {
            Person person = addPerson(registerRq);
            personRepository.save(person);
        } else {
            validationUtils.checkUserAvailability(registerRq.getEmail());
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


