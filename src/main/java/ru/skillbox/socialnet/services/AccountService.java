package ru.skillbox.socialnet.services;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import ru.skillbox.socialnet.data.entity.Person;
import ru.skillbox.socialnet.dto.request.PersonRs;
import ru.skillbox.socialnet.dto.request.response.CommonRsPersonRs;
import ru.skillbox.socialnet.model.RegistrationInfo;
import ru.skillbox.socialnet.repository.PersonRepository;
import ru.skillbox.socialnet.utils.ValidationUtilsRq;

import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AccountService {
    public PersonRepository personRepository;
    public final ValidationUtilsRq validationUtils;

    public ResponseEntity<CommonRsPersonRs<PersonRs>> registration(RegistrationInfo registrationInfo) {
        validationUtils.validationRegPassword(registrationInfo.getPasswd1(), registrationInfo.getPasswd2());
        validationUtils.validationCode(registrationInfo.getCode(), registrationInfo.getCodeSecret());

        Person person = addPerson(registrationInfo);
        personRepository.save(person);
        CommonRsPersonRs<PersonRs> response = new CommonRsPersonRs<>();
        List<PersonRs> data = new ArrayList<>();
        PersonRs personRs = new PersonRs();
        personRs.setEmail(person.getEmail());
        data.add(personRs);
        response.setData(data);
        return ResponseEntity.ok(response);
    }

    private Person addPerson(RegistrationInfo registrationInfo) {
        Person person = new Person();
        person.setEmail(registrationInfo.getEmail());
        person.setPassword(getEncodedPassword(registrationInfo.getPasswd1()));
        person.setFirstName(registrationInfo.getFirstName());
        person.setLastName(registrationInfo.getLastName());
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


