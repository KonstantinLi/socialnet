package ru.skillbox.socialnet.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.skillbox.socialnet.data.entity.Person;

import java.util.Optional;

@Repository
public interface PersonRepository extends CrudRepository<Person, Long> {
    Optional<Person> findByemail(String email);

    /**
     *
     * @param currentPersonId - id текущей персоны
     * @return - запрос вернет друзей текущей персоны (переданной в параметре)
     */
    @Query(value = "select p from persons p where p.id in " +
            "(select f.dst_person_id from friendships f " +
            "where f.src_person_id = :currentPersonId " +
            "and f.ftatus_name = \"FRIEND\")")
    Page<Person> findPersonsByFriendship(@Param("currentPersonId") long currentPersonId, Pageable pageable);

    @Query(value = "select count(p) from persons p where p.id in " +
            "(select f.dst_person_id from friendships f " +
            "where f.src_person_id = :currentPersonId " +
            "and f.ftatus_name = \"FRIEND\")")
    long findCountPersonsByFriendship(@Param("currentPersonId") long currentPersonId);
}
