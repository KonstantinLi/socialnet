package ru.skillbox.socialnet.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.skillbox.socialnet.dto.PersonRs;
import ru.skillbox.socialnet.dto.response.CommonRsPersonRs;
import ru.skillbox.socialnet.entity.Person;
import ru.skillbox.socialnet.exception.ExceptionBadRq;
import ru.skillbox.socialnet.repository.PersonRepository;
import ru.skillbox.socialnet.security.util.JwtTokenUtils;
import ru.skillbox.socialnet.util.ValidationUtilsRq;
import ru.skillbox.socialnet.util.mapper.PersonMapper;

import java.util.Collection;
import java.util.Date;

@Service
@RequiredArgsConstructor
public class UserService {
    public final ValidationUtilsRq validationUtils;
    private final JwtTokenUtils jwtTokenUtils;
    private final PersonRepository personRepository;

    public CommonRsPersonRs<PersonRs> userMe(String token) throws ExceptionBadRq {
        Long id = jwtTokenUtils.getId(token);
        Person person = personRepository.findById(id).orElseThrow(
                () -> new ExceptionBadRq("Пользователь не найден"));
        ru.skillbox.socialnet.dto.PersonRs personRs = PersonMapper.INSTANCE.personToPersonRs(person, "", false);
        personRs.setToken(token);
        CommonRsPersonRs<PersonRs> response = new CommonRsPersonRs<>();
        response.setTimeStamp(new Date().getTime());
        response.setData(personRs);
        return response;
    }
}
