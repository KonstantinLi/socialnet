package ru.skillbox.socialnet.services;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;
import ru.skillbox.socialnet.dto.request.response.ComplexRs;
import ru.skillbox.socialnet.dto.request.response.RegisterRs;
import ru.skillbox.socialnet.entity.Person;
import ru.skillbox.socialnet.entity.PersonSettings;
import ru.skillbox.socialnet.exception.CommonException;
import ru.skillbox.socialnet.model.RegisterRq;
import ru.skillbox.socialnet.repository.PersonRepository;
import ru.skillbox.socialnet.repository.PersonSettingsRepository;
import ru.skillbox.socialnet.util.ValidationUtilsRq;

import java.util.Base64;
import java.util.Date;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AccountService {
    public final PersonRepository personRepository;
    public final ValidationUtilsRq validationUtils;
    public final PersonSettingsRepository personSettingsRepository;

    public RegisterRs<ComplexRs> registration(RegisterRq registerRq) throws CommonException {
        validationUtils.validationRegPassword(registerRq.getPasswd1(), registerRq.getPasswd2());
        validationUtils.validationCode(registerRq.getCode(), registerRq.getCodeSecret());
        Person person = null;
        if (findPersonByEmail(registerRq.getEmail()) == null) {
            person = addPerson(registerRq);
            personRepository.save(person);
        } else {
            validationUtils.checkUserAvailability(registerRq.getEmail());
        }
        RegisterRs<ComplexRs> response = new RegisterRs<>();
        ComplexRs complexRs = new ComplexRs();
        response.setData(complexRs);
        response.setEmail(person.getEmail());
        response.setTimestamp(new Date().getTime());
        return response;
    }

    private Optional<Person> findPersonByEmail(String email) throws CommonException {
        Optional<Person> personRes = null;
        try {
            personRes = personRepository.findByEmail(email);
        } catch (BadCredentialsException ex) {
            validationUtils.validationUser();
        }
        return personRes;
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


