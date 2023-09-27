package ru.skillbox.socialnet.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.skillbox.socialnet.dto.response.CommonRsPersonRs;
import ru.skillbox.socialnet.dto.response.PersonRs;
import ru.skillbox.socialnet.entity.enums.FriendShipStatus;
import ru.skillbox.socialnet.errs.BadRequestException;
import ru.skillbox.socialnet.entity.Person;
import ru.skillbox.socialnet.repository.PersonRepository;
import ru.skillbox.socialnet.security.util.JwtTokenUtils;
import ru.skillbox.socialnet.util.mapper.PersonMapper;

import java.util.Date;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PersonService {

    private final PersonRepository personRepository;
    private final JwtTokenUtils jwtTokenUtils;

    public CommonRsPersonRs<PersonRs> getUserById(Long otherUserId, Long currentUserId) throws BadRequestException {
        Optional<Person> optional = personRepository.findById(otherUserId);
        if (optional.isEmpty()) {
            throw new BadRequestException("Пользователь с указанным id не найден");
        }
        Person person = optional.get();

        PersonRs personRs = PersonMapper.INSTANCE.personToPersonRs(person, FriendShipStatus.UNKNOWN.name(), false);
        CommonRsPersonRs<PersonRs> result = new CommonRsPersonRs<>();
        result.setData(personRs);
        return result;
    }

    public CommonRsPersonRs<PersonRs> userMe(String token) throws BadRequestException {
        Long id = jwtTokenUtils.getId(token);
        Person person = personRepository.findById(id).orElseThrow(
                () -> new BadRequestException("Пользователь не найден"));
        PersonRs personRs = PersonMapper.INSTANCE.personToPersonRs(person, "", false);
        personRs.setToken(token);
        CommonRsPersonRs<PersonRs> response = new CommonRsPersonRs<>();
        response.setTimeStamp(new Date().getTime());
        response.setData(personRs);
        return response;
    }
}
