package ru.skillbox.socialnet.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.skillbox.socialnet.entity.Person;

import java.util.Optional;

@Repository
public interface PersonRepository extends CrudRepository<Person, Long> {
    @Query(value = "SELECT p FROM Person p WHERE p.email = :email")
    Optional<Person> findPesonByemail( @Param("email") String email);

    /**
     *
     * @param currentPersonId - текущая персона
     * @return - запрос вернет друзей текущей персоны (переданной в параметре)
     */

    @Query(value = "select * from persons p where p.id in " +
            "(select f.dst_person_id from friendships f " +
            "where f.src_person_id = :currentPersonId " +
            "and f.status_name = :status_name)",
           countQuery ="select COUNT(*) from persons p where p.id in " +
                  "(select f.dst_person_id from friendships f " +
                  "where f.src_person_id = :currentPersonId " +
                  "and f.status_name = :status_name)",
           nativeQuery = true)
    Page<Person> findPersonsByFriendship(@Param("currentPersonId") long currentPersonId,
                                         @Param("status_name") String statusName,
                                         Pageable pageable);


    @Query(value = "select count(p) from persons p where p.id in " +
            "(select f.dst_person_id from friendships f " +
            "where f.src_person_id = :currentPersonId " +
            "and f.status_name = :status_name)", nativeQuery = true)
    long findCountPersonsByFriendship(@Param("currentPersonId") long currentPersonId,
                                      @Param("status_name") String statusName);
}
