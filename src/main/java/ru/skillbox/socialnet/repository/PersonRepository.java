package ru.skillbox.socialnet.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.skillbox.socialnet.dto.response.RegionStatisticsRs;
import ru.skillbox.socialnet.entity.personrelated.Person;
import ru.skillbox.socialnet.exception.PersonNotFoundException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface PersonRepository extends JpaRepository<Person, Long> {
    @Query("select p from Person p where p.isDeleted = true and p.deletedTime is null")
    List<Person> findByIsDeletedTrueAndDeletedTimeNull();

    @Query("select p from Person p where p.isDeleted = ?1 and p.deletedTime < ?2")
    List<Person> findByIsDeletedAndDeletedTimeBefore(Boolean isDeleted, LocalDateTime deletedTime);

    List<Person> findByDeletedTimeBefore(LocalDateTime deletedTime);

    long countByIsDeletedFalseOrIsDeletedNull();

    /**
     * @param personIds - список персон, для которых будет сгенерирован список предложений в друзья
     * @param cnt       - кол-во сгенерированных записей (должно быть меньше 100)
     * @return - запрос сгенерирует cnt рандомных персон (как друзей) для списка персон personIds
     */
    @Query(value = "select distinct p.* from persons p, " +
            "(select distinct round(random() * (select max(id) - 1 from persons)) + " +
            "1 as id from generate_series (1, 100)) t " +
            "where p.id = t.id and  t.id not in (:personIds) " +
            "and (p.is_blocked = false or p.is_blocked is null) " +
            "and (p.is_deleted = false or p.is_deleted is null) " +
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
    @Query(value =
            "SELECT DISTINCT P.* FROM FRIENDSHIPS F, PERSONS P WHERE F.DST_PERSON_ID = P.ID " +
                    "AND F.SRC_PERSON_ID IN (SELECT FF.DST_PERSON_ID FROM FRIENDSHIPS FF WHERE FF.SRC_PERSON_ID = :personId " +
                    "AND FF.STATUS_NAME = 'FRIEND') AND F.DST_PERSON_ID != :personId  " +
                    "AND (P.IS_BLOCKED = FALSE OR P.IS_BLOCKED IS NULL) AND (P.IS_DELETED = FALSE OR P.IS_DELETED IS NULL) " +
                    "AND NOT EXISTS (SELECT 1 FROM FRIENDSHIPS F1 WHERE F1.SRC_PERSON_ID = P.ID AND F1.STATUS_NAME = 'FRIEND') " +
                    "UNION " +
                    "SELECT DISTINCT P.* FROM PERSONS P, " +
                    "(SELECT DISTINCT ROUND(RANDOM() * (SELECT MAX(ID) - 1 FROM PERSONS)) + 1 AS ID " +
                    "FROM GENERATE_SERIES (1, 100)) T WHERE P.ID = T.ID AND T.ID != :personId " +
                    "AND (P.IS_BLOCKED = FALSE OR P.IS_BLOCKED IS NULL) AND (P.IS_DELETED = FALSE OR P.IS_DELETED IS NULL) " +
                    "AND NOT EXISTS (SELECT 1 FROM FRIENDSHIPS F1 WHERE F1.SRC_PERSON_ID = P.ID AND F1.STATUS_NAME = 'FRIEND') " +
                    "FETCH FIRST 10 ROWS ONLY"
            , nativeQuery = true)
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

    @Query(nativeQuery = true, value = """
            select *
            from persons
            where id != :currentPersonId
            and (is_blocked is null or not is_blocked)
            and (is_deleted is null or not is_deleted)
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

    @Query("SELECT p FROM Person p WHERE MONTH(p.birthDate) = :month AND DAY(p.birthDate) = :day")
    List<Person> findAllByBirthDate(@Param("month") int month, @Param("day") int day);

    long countByIsDeleted(boolean isDeleted);

    long countByCountryAndIsDeleted(String country, boolean isDeleted);

    long countByCityAndIsDeleted(String city, boolean isDeleted);

    @Query(value = "SELECT country AS region, COUNT(country) AS countUsers"
            + " FROM persons"
            + " GROUP BY country"
            + " ORDER BY country ASC"
            , nativeQuery = true
    )
    Collection<RegionStatisticsRs> countCountryStatistics();

    @Query(value = "SELECT city AS region, COUNT(city) AS countUsers"
            + " FROM persons"
            + " GROUP BY city"
            + " ORDER BY city ASC"
            , nativeQuery = true
    )
    Collection<RegionStatisticsRs> countCityStatistics();

    @Query(value = "select * from persons p where p.deleted_time < :timeParam", nativeQuery = true)
    Optional<List<Person>> findAllInactiveUsersByDeletedTime(@Param("timeParam") LocalDateTime timeParam);

    @Query(nativeQuery = true, value = "select distinct city from persons")
    ArrayList<String> findDistinctCity();
}
