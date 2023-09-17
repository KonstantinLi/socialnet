package ru.skillbox.socialnet.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.skillbox.socialnet.dto.PersonRs;
import ru.skillbox.socialnet.entity.Person;
import ru.skillbox.socialnet.repository.PersonRepository;
import ru.skillbox.socialnet.util.mapper.PersonMapper;

import java.util.Optional;

@Service
public class UserService {

    private final PersonRepository personRepository;
    private final PersonMapper personMapper;

    @Autowired
    public UserService(PersonRepository personRepository, PersonMapper personMapper) {
        this.personRepository = personRepository;
        this.personMapper = personMapper;
    }

    public PersonRs getUserById(Long userId) {
        Optional<Person> optional = personRepository.findById(userId);
        return optional.map(personMapper::toRs).orElse(null);
    }
}
