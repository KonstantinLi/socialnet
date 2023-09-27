package ru.skillbox.socialnet.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.skillbox.socialnet.dto.PersonRs;
import ru.skillbox.socialnet.dto.response.CommonRs;
import ru.skillbox.socialnet.entity.enums.FriendShipStatus;
import ru.skillbox.socialnet.errs.BadRequestException;
import ru.skillbox.socialnet.entity.Person;
import ru.skillbox.socialnet.repository.PersonRepository;
import ru.skillbox.socialnet.util.mapper.PersonMapper;

import java.util.Collection;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PersonService {

    private final PersonRepository personRepository;

    public CommonRs<PersonRs> getUserById(Long otherUserId, Long currentUserId) throws BadRequestException {
        Optional<Person> optional = personRepository.findById(otherUserId);
        if (optional.isEmpty()) {
            throw new BadRequestException("Пользователь с указанным id не найден");
        }
        Person person = optional.get();

        PersonRs personRs = PersonMapper.INSTANCE.personToPersonRs(person, FriendShipStatus.UNKNOWN.name(), false);
        CommonRs<PersonRs> result = new CommonRs<>();
        result.setData(personRs);
        return result;
    }
}
