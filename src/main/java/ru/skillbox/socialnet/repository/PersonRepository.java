package ru.skillbox.socialnet.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.skillbox.socialnet.entity.Person;

@Repository
public interface PersonRepository extends JpaRepository<Person, Long> {

}
