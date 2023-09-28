package ru.skillbox.socialnet.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.skillbox.socialnet.entity.locationrelated.Weather;

@Repository
public interface WeatherRepository extends JpaRepository<Weather, Long> {

    @Query(value = """
            select * from weather where city = :city
            order by date desc
            limit 1
            """, nativeQuery = true)
    Weather findLastByCity(@Param("city") String city);
}
