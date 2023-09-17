package ru.skillbox.socialnet.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.skillbox.socialnet.entity.Person;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public interface PersonRepository extends CrudRepository<Person, Long> {
    /**
     *
     * @param email - email персоны
     * @return      - находим персону по email
     */
    @Query(value = "SELECT p FROM Person p WHERE p.email = :email")
    Optional<Person> findPesonByemail( @Param("email") String email);

    /**
     *
     * @param personIds - список персон, для которых будет сгенерирован список предложений в друзья
     * @param cnt  - кол-во сгенерированных записей (должно быть меньше 100)
     * @return  - запрос сгенерирует cnt рандомных персон (как друзей) для списка персон personIds
     */
    @Query(value = "select p.* from persons p, " +
            "  (select distinct round(random() * (select max(id) - 1 from persons)) + 1 as id from generate_series (1, 100)) t " +
            "where p.id = t.id and  t.id not in (:personIds)" +
            "  fetch first :cnt rows only", nativeQuery = true)
    Iterable<Person> randomGenerateFriendsForPerson(@Param("personIds") List<Long> personIds,
                                                    @Param("cnt") long cnt);

    /**
     *
     * @param personId - id персоны
     * @return - запрос вернет друзей у друзей персоны, переданной в параметре
     */
    @Query(value = "select p.* from friendships f, persons p where f.dst_person_id = p.id and f.src_person_id in  " +
            " (select ff.dst_person_id from friendships ff where ff.src_person_id = :personId and ff.status_name = 'FRIEND') " +
            " and f.dst_person_id != :personId", nativeQuery = true)
    Iterable<Person> getFriendsOfFriendsByPersonId(@Param("personId") long personId);

    /**
     *
     * @param currentPersonId - текущая персона
     * @return - запрос вернет друзей текущей персоны (переданной в параметре)
     */
    @Query(value = "select * from persons p where p.id in " +
            "(select f.dst_person_id from friendships f " +
            "where f.src_person_id = :currentPersonId " +
            "and f.status_name = :status_name)",
           nativeQuery = true)
    Page<Person> findPersonsByFriendship(@Param("currentPersonId") long currentPersonId,
                                         @Param("status_name") String statusName,
                                         Pageable pageable);

    /**
     *
     * @param currentPersonId - id текущей персоны
     * @param statusName      - статус в таблице Friendship где текущая персона = src_person_id
     * @return - запрос вернет количество персон, которые имеют определенный статус в таблице Friendship с текущей персоной
     */
    @Query(value = "select count(p) from persons p where p.id in " +
            "(select f.dst_person_id from friendships f " +
            "where f.src_person_id = :currentPersonId " +
            "and f.status_name = :status_name)", nativeQuery = true)
    long findCountPersonsByFriendship(@Param("currentPersonId") long currentPersonId,
                                      @Param("status_name") String statusName);
}
