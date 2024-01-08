package com.socialnet.service;

import com.socialnet.repository.PersonRepository;
import com.socialnet.repository.WeatherRepository;
import com.socialnet.service.weather.WeatherCityUpdater;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

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
        for (String city : cities) {
            updateCity(city);
        }
    }

    private void updateCity(String city) {
        WeatherCityUpdater weatherCityUpdater = new WeatherCityUpdater(weatherRepository, city, apiKey);
        weatherCityUpdater.start();
    }
}
