package ru.skillbox.socialnet.services;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;
import ru.skillbox.socialnet.dto.request.PersonRs;
import ru.skillbox.socialnet.dto.request.response.CommonRsPersonRs;
import ru.skillbox.socialnet.entity.Person;
import ru.skillbox.socialnet.exception.CommonException;
import ru.skillbox.socialnet.repository.PersonRepository;
import ru.skillbox.socialnet.security.util.JwtTokenUtils;
import ru.skillbox.socialnet.util.ValidationUtilsRq;
import ru.skillbox.socialnet.util.mapper.PersonMapper;

import java.util.Date;

@Service
@RequiredArgsConstructor
public class UserService {
    public final ValidationUtilsRq validationUtils;
    private final JwtTokenUtils jwtTokenUtils;
    private final PersonRepository personRepository;
    public CommonRsPersonRs<PersonRs> userMe(String token) throws CommonException {
        Person person = null;
        Long id = jwtTokenUtils.getId(token);
        try {
            person = personRepository.findById(id).orElseThrow();
        } catch (BadCredentialsException ex) {
            validationUtils.validationUser();
        }
        PersonRs personRs = PersonMapper.INSTANCE.toRs(person);
        personRs.setToken(token);
        CommonRsPersonRs<PersonRs> response = new CommonRsPersonRs<>();
        response.setTimeStamp(new Date().getTime());
        response.setData(personRs);
        return response;
    }
}
