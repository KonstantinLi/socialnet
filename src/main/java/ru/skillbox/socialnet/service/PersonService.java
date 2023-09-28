package ru.skillbox.socialnet.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.skillbox.socialnet.dto.response.PersonRs;
import ru.skillbox.socialnet.dto.response.CommonRs;
import ru.skillbox.socialnet.entity.personrelated.Person;
import ru.skillbox.socialnet.exception.BadRequestException;
import ru.skillbox.socialnet.mapper.PersonMapper;
import ru.skillbox.socialnet.repository.PersonRepository;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PersonService {
    private final PersonMapper personMapper;
    private final PersonRepository personRepository;

    public CommonRs<PersonRs> getUserById(Long otherUserId, Long currentUserId) throws BadRequestException {
        Optional<Person> optional = personRepository.findById(otherUserId);
        if (optional.isEmpty()) {
            throw new BadRequestException("Пользователь с указанным id не найден");
        }
        Person person = optional.get();

        PersonRs personRs = personMapper.personToPersonRs(person);
        CommonRs<PersonRs> result = new CommonRs<>();
        result.setData(personRs);
        return result;
    }
}
