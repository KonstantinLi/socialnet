package ru.skillbox.socialnet.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.skillbox.socialnet.entity.personrelated.Person;
import ru.skillbox.socialnet.repository.PersonRepository;

import java.util.Optional;

@RestController
@RequestMapping("/test")
@RequiredArgsConstructor
public class TestController {

    private final PersonRepository personRepository;

    @GetMapping()
    public String test() {

        Optional<Person> optionalPerson = personRepository.findByEmail("mmccreedyc@hibu.com");

        if (optionalPerson.isEmpty()) {
            return "Not found";
        }

        Person person = optionalPerson.get();


        return "pass: " + person.getPassword();
    }
}
