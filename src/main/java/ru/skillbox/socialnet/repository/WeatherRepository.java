package ru.skillbox.socialnet.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.skillbox.socialnet.entity.other.Weather;

import java.util.Optional;

@Repository
public interface WeatherRepository extends CrudRepository<Weather, Long> {
    Optional<Weather> findByCity(String city);
}
