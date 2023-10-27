package ru.skillbox.socialnet.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.skillbox.socialnet.entity.locationrelated.Currency;

import java.util.Optional;

public interface CurrencyRepository extends JpaRepository<Currency, Long> {
    Optional<Currency> findByName(String valName);

    @Query(nativeQuery = true, value = "select price from currencies where name ilike :name order by id desc limit 1")
    String findPriceByName(@Param("name") String name);
}