package ru.skillbox.socialnet.repository;

import org.springframework.data.repository.CrudRepository;
import ru.skillbox.socialnet.entity.Person;

import java.util.Set;

public interface PersonsRepository extends CrudRepository<Person, Long> {
    Set<Person> findAllByFirstNameAndLastName(String firstName, String lastName);
    Set<Person> findAllByFirstName(String firstName);
    Set<Person> findAllByLastName(String lastName);
}
