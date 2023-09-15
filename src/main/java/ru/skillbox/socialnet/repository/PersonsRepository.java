package ru.skillbox.socialnet.repository;

import org.springframework.data.repository.CrudRepository;
import ru.skillbox.socialnet.entity.Person;

public interface PersonsRepository extends CrudRepository<Person, Long> {
}
