package ru.skillbox.socialnet.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.skillbox.socialnet.dto.request.PersonRs;
import ru.skillbox.socialnet.dto.request.response.CommonRsPersonRs;
import ru.skillbox.socialnet.entity.Person;
import ru.skillbox.socialnet.exception.ExceptionBadRq;
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

    public CommonRsPersonRs<PersonRs> userMe(String token) throws ExceptionBadRq {
        Long id = jwtTokenUtils.getId(token);
        Person person = personRepository.findById(id).orElseThrow(
                () -> new ExceptionBadRq("Пользователь не найден"));
        PersonRs personRs = PersonMapper.INSTANCE.toRs(person);
        personRs.setToken(token);
        CommonRsPersonRs<PersonRs> response = new CommonRsPersonRs<>();
        response.setTimestamp(new Date().getTime());
        response.setData(personRs);
        return response;
    }
}
