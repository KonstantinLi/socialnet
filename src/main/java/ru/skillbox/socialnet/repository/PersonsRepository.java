package ru.skillbox.socialnet.repository;

import org.springframework.data.repository.CrudRepository;
import ru.skillbox.socialnet.entity.personrelated.Person;

import java.util.Set;

public interface PersonsRepository extends CrudRepository<Person, Long> {
    Set<Person> findAllByFirstNameAndLastNameAndIsDeleted(String firstName, String lastName, boolean isDeleted);
    Set<Person> findAllByFirstNameAndIsDeleted(String firstName, boolean isDeleted);
    Set<Person> findAllByLastNameAndIsDeleted(String lastName, boolean isDeleted);
}
