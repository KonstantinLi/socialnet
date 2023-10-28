package ru.skillbox.socialnet.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.skillbox.socialnet.repository.PersonRepository;
import ru.skillbox.socialnet.repository.WeatherRepository;
import ru.skillbox.socialnet.service.weather.WeatherCityUpdater;

import java.util.List;

@Service
@RequiredArgsConstructor
public class WeatherService {

    private final PersonRepository personRepository;
    private final WeatherRepository weatherRepository;

    @Value("${openweathermap.api_key}")
    private String apiKey;

    public void updateAllCities() {
        List<String> cities = personRepository.findDistinctCity();
        for (String city: cities) {
            updateCity(city);
        }
    }

    private void updateCity(String city) {
        WeatherCityUpdater weatherCityUpdater = new WeatherCityUpdater(weatherRepository, city, apiKey);
        weatherCityUpdater.start();
    }
}
