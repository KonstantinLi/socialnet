package ru.skillbox.socialnet.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.skillbox.socialnet.entity.locationrelated.Currency;

public interface CurrencyRepository extends JpaRepository<Currency, Long> {
}