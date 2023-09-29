package ru.skillbox.socialnet.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.skillbox.socialnet.dto.response.PersonRs;
import ru.skillbox.socialnet.dto.response.CommonRs;
import ru.skillbox.socialnet.entity.personrelated.Person;
import ru.skillbox.socialnet.exception.old.ExceptionBadRq;
import ru.skillbox.socialnet.mapper.PersonMapper;
import ru.skillbox.socialnet.repository.PersonRepository;
import ru.skillbox.socialnet.security.JwtTokenUtils;
import ru.skillbox.socialnet.util.ValidationUtilsRq;

@Service
@RequiredArgsConstructor
public class UserService {
    public final ValidationUtilsRq validationUtils;
    private final JwtTokenUtils jwtTokenUtils;
    private final PersonRepository personRepository;
    private final PersonMapper personMapper;

    public CommonRs<PersonRs> userMe(String token) throws ExceptionBadRq {
        Long id = jwtTokenUtils.getId(token);
        Person person = personRepository.findById(id).orElseThrow(
                () -> new ExceptionBadRq("Пользователь не найден"));
        PersonRs personRs = personMapper.personToPersonRs(person, "", false);
        personRs.setToken(token);
        CommonRs<PersonRs> response = new CommonRs<>();
        response.setData(personRs);
        return response;
    }
}
