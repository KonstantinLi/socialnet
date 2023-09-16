package ru.skillbox.socialnet.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.skillbox.socialnet.data.entity.Person;

import java.util.List;


@Repository
public interface PersonRepository extends CrudRepository<Person, Long> {

    @Query("SELECT p.id FROM Person p")
    List<Long> findAllId();
}
