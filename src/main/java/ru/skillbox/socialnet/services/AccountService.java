package ru.skillbox.socialnet.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.skillbox.socialnet.data.entity.Person;
import ru.skillbox.socialnet.dto.request.response.ComplexRs;
import ru.skillbox.socialnet.dto.request.response.RegisterRs;
import ru.skillbox.socialnet.exception.CommonException;
import ru.skillbox.socialnet.model.RegisterRq;
import ru.skillbox.socialnet.repository.PersonRepository;
import ru.skillbox.socialnet.util.ValidationUtilsRq;

import java.security.Timestamp;
import java.util.Base64;
import java.util.Date;

@Service
@RequiredArgsConstructor
public class AccountService {
    public final PersonRepository personRepository;
    public final ValidationUtilsRq validationUtils;

    public RegisterRs<ComplexRs> registration(RegisterRq registerRq) throws CommonException {
        validationUtils.validationRegPassword(registerRq.getPasswd1(), registerRq.getPasswd2());
        validationUtils.validationCode(registerRq.getCode(), registerRq.getCodeSecret());

        Person person = addPerson(registerRq);
        personRepository.save(person);
        RegisterRs<ComplexRs> response = new RegisterRs<>();
        ComplexRs complexRs = new ComplexRs();
        response.setData(complexRs);
        response.setEmail(person.getEmail());
        response.setTimestamp(new Date().getTime()); //todo long or int?
        return response;
    }

    private Person addPerson(RegisterRq registrationInfo) {
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


