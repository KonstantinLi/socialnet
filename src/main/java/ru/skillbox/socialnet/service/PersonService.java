package ru.skillbox.socialnet.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.skillbox.socialnet.dto.response.PersonRs;
import ru.skillbox.socialnet.dto.response.CommonRs;
import ru.skillbox.socialnet.dto.service.GetUsersSearchPs;
import ru.skillbox.socialnet.exception.BadRequestException;
import ru.skillbox.socialnet.entity.personrelated.Person;
import ru.skillbox.socialnet.repository.PersonRepository;
import ru.skillbox.socialnet.util.mapper.PersonMapper;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PersonService {

    private final PersonRepository personRepository;

    public CommonRs<PersonRs> getUserById(Long otherUserId) throws BadRequestException {
        Optional<Person> optional = personRepository.findById(otherUserId);
        if (optional.isEmpty()) {
            throw new BadRequestException("Пользователь с указанным id не найден");
        }
        Person person = optional.get();
        PersonRs personRs = PersonMapper.INSTANCE.toRs(person);
        CommonRs<PersonRs> result = new CommonRs<>();
        result.setData(personRs);
        return result;
    }

    public CommonRs<List<PersonRs>> getUsersByQuery(Long currentUserId,
                                                    GetUsersSearchPs getUsersSearchPs,
                                                    int offset,
                                                    int perPage) {
        Pageable nextPage = PageRequest.of(offset, perPage);
        CommonRs<List<PersonRs>> result = new CommonRs<>();
        Page<Person> personPage = personRepository.findUsersByQuery(currentUserId,
                getUsersSearchPs.getAgeFrom(),
                getUsersSearchPs.getAgeTo(),
                getUsersSearchPs.getCity(),
                getUsersSearchPs.getCountry(),
                getUsersSearchPs.getFirstName(),
                getUsersSearchPs.getLastName(),
                nextPage);
        result.setData(PersonMapper.INSTANCE.toRsList(personPage.getContent()));
        result.setTotal(personPage.getTotalElements());
        result.setItemPerPage(personPage.getContent().size());
        result.setPerPage(perPage);
        result.setOffset(offset);

        return result;
    }
}
