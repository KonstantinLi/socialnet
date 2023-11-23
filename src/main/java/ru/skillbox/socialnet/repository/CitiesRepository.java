package ru.skillbox.socialnet.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.skillbox.socialnet.dto.response.RegionStatisticsRs;
import ru.skillbox.socialnet.entity.locationrelated.City;

import java.util.List;

@Repository
public interface CitiesRepository extends JpaRepository<City, Long> {
    @Query(value = "SELECT t.name AS region, COUNT(p.country) AS countUsers"
            + " FROM cities t LEFT JOIN persons p ON t.name = p.country"
            + " GROUP BY t.name ORDER BY t.name ASC"
            , nativeQuery = true
    )
    List<RegionStatisticsRs> countRegionStatistics();


    @Query("select c from City c where c.country.name = :country and c.name like %:starts ")
    List<City> getCitiesByCountryAndStarts(String country, String starts);

    @Query("select c from City c, Person p where c.name = p.city and c.country.name = :country order by c.name")
    List<City> getCitiesByCountryUses(String country);

}
