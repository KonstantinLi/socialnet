package ru.skillbox.socialnet.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.skillbox.socialnet.entity.personrelated.Person;
import ru.skillbox.socialnet.exception.person.PersonNotFoundException;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface PersonRepository extends JpaRepository<Person, Long> {

    /**
     * @param personIds - список персон, для которых будет сгенерирован список предложений в друзья
     * @param cnt       - кол-во сгенерированных записей (должно быть меньше 100)
     * @return - запрос сгенерирует cnt рандомных персон (как друзей) для списка персон personIds
     */
    @Query(value = "select p.* from persons p, " +
            "(select distinct round(random() * (select max(id) - 1 from persons)) + " +
            "1 as id from generate_series (1, 100)) t " +
            "where p.id = t.id and  t.id not in (:personIds) " +
            "fetch first :cnt rows only", nativeQuery = true)
    Iterable<Person> randomGenerateFriendsForPerson(@Param("personIds") List<Long> personIds,
                                                    @Param("cnt") long cnt);

    /**
     * @param id - ID персоны
     * @return - дефолтный метод-обертка вернет персону или сгенерирует исключение
     * @throws PersonNotFoundException - может быть сгенерировано исключение,
     *                                 если запись в таблице не найдена
     */
    default Person findByIdImpl(Long id) throws PersonNotFoundException {

        Optional<Person> personOptional = findById(id);

        if (personOptional.isPresent()) {
            return personOptional.get();
        } else {
            throw new PersonNotFoundException("Запись о профиле не найдена");
        }
    }

    /**
     * @param personId - id персоны
     * @return - запрос вернет друзей у друзей персоны, переданной в параметре
     */
    @Query(value = "select p.* from friendships f, " +
            "persons p where f.dst_person_id = p.id and f.src_person_id in  " +
            "(select ff.dst_person_id from friendships ff " +
            "where ff.src_person_id = :personId and ff.status_name = 'FRIEND') " +
            "and f.dst_person_id != :personId", nativeQuery = true)
    Iterable<Person> getFriendsOfFriendsByPersonId(@Param("personId") long personId);

    /**
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

    Optional<Person> findByEmail(String email);

    Set<Person> findAllByFirstNameAndLastNameAndIsDeleted(String firstName, String lastName, boolean isDeleted);
    Set<Person> findAllByFirstNameAndIsDeleted(String firstName, boolean isDeleted);
    Set<Person> findAllByLastNameAndIsDeleted(String lastName, boolean isDeleted);

    @Query(nativeQuery = true, value = """
            select *
            from persons
            where id != :currentPersonId
            and case when :ageFrom = 0 then true else date_part('year', age(current_date, birth_date)) >= :ageFrom end
            and case when :ageTo = 0 then true else date_part('year', age(current_date, birth_date)) <= :ageTo end
            and case when cast(:city as varchar) is null then true else city ilike :city end
            and case when cast(:country as varchar) is null then true else country ilike :country end
            and case when cast(:firstName as varchar) is null then true
                else first_name ilike concat('%', cast(:firstName as varchar), '%') end
            and case when cast(:lastName as varchar) is null then true
                else last_name ilike concat('%', cast(:lastName as varchar), '%') end
            """)
    Page<Person> findUsersByQuery(@Param("currentPersonId") Long currentPersonId,
                                  @Param("ageFrom") int ageFrom,
                                  @Param("ageTo") int ageTo,
                                  @Param("city") String city,
                                  @Param("country") String country,
                                  @Param("firstName") String firstName,
                                  @Param("lastName") String lastName,
                                  Pageable nextPage);
}
