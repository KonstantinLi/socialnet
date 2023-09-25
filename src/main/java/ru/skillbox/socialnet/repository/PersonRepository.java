package ru.skillbox.socialnet.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.skillbox.socialnet.entity.Person;

import java.util.Optional;


@Repository
public interface PersonRepository extends JpaRepository<Person, Long> {
    Optional<Person> findByEmail(String email);

    @Query(nativeQuery = true, value = """
            select *
            from persons
            where id != :currentPersonId
            and case when :ageFrom = 0 then true else date_part('year', age(current_date, birth_date)) >= :ageFrom end
            and case when :ageTo = 0 then true else date_part('year', age(current_date, birth_date)) <= :ageTo end
            and case when cast (:city as varchar) is null then true else city ilike :city end
            and case when cast (:country as varchar) is null then true else country ilike :country end
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
