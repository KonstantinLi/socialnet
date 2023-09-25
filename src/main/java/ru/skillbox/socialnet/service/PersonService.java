package ru.skillbox.socialnet.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.skillbox.socialnet.dto.PersonRs;
import ru.skillbox.socialnet.dto.response.CommonRsPersonRs;
import ru.skillbox.socialnet.errs.BadRequestException;
import ru.skillbox.socialnet.entity.Person;
import ru.skillbox.socialnet.repository.PersonRepository;
import ru.skillbox.socialnet.util.mapper.PersonMapper;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PersonService {

    private final PersonRepository personRepository;

    public CommonRsPersonRs<PersonRs> getUserById(Long otherUserId, Long currentUserId) throws BadRequestException {
        Optional<Person> optional = personRepository.findById(otherUserId);
        if (optional.isEmpty()) {
            throw new BadRequestException("Пользователь с указанным id не найден");
        }
        Person person = optional.get();
        PersonRs personRs = PersonMapper.INSTANCE.toRs(person);
        CommonRsPersonRs<PersonRs> result = new CommonRsPersonRs<>();
        result.setData(personRs);
        return result;
    }
}
