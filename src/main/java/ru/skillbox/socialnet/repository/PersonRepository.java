package ru.skillbox.socialnet.repository;

import jakarta.transaction.Transactional;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.skillbox.socialnet.data.entity.Person;

@Repository
@Transactional
public interface PersonRepository extends CrudRepository<Person, Long> {
    Person findByEmail(String email);


}
