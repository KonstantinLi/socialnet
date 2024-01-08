package com.socialnet.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import com.socialnet.dto.response.RegionStatisticsRs;
import com.socialnet.entity.locationrelated.Country;

import java.util.List;
import java.util.Optional;

@Repository
public interface CountriesRepository extends JpaRepository<Country, Long> {
    @Query(value = "SELECT t.name AS region, COUNT(p.country) AS countUsers"
            + " FROM countries t LEFT JOIN persons p ON t.name = p.country"
            + " GROUP BY t.name ORDER BY t.name ASC"
            , nativeQuery = true
    )
    List<RegionStatisticsRs> countRegionStatistics();

    Optional<Country> findCountryByName(String name);
}
