package ru.skillbox.socialnet.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import ru.skillbox.socialnet.annotation.DebugLoggable;
import ru.skillbox.socialnet.dto.response.openweathermap.OpenWeatherMapRs;
import ru.skillbox.socialnet.entity.locationrelated.Weather;
import ru.skillbox.socialnet.repository.PersonRepository;
import ru.skillbox.socialnet.repository.WeatherRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class WeatherService {

    private final PersonRepository personRepository;
    private final WeatherRepository weatherRepository;

    @Value("${openweathermap.api_key}")
    private String apiKey;

    private static final String WEATHER_URL = "https://api.openweathermap.org/data/2.5/weather";

    public void updateAllCities() {
        List<String> cities = personRepository.findDistinctCity();
        for (String city: cities) {
            updateCiti(city);
        }
    }

    private void updateCiti(String city) {
        OpenWeatherMapRs openWeatherMapRs = getWeather(city);
        if (openWeatherMapRs == null) {
            return;
        }

        Optional<Weather> optWeather = weatherRepository.findByCity(city);
        Weather weather = optWeather.orElseGet(Weather::new);
        weather.setCity(city);
        weather.setDate(LocalDateTime.now());
        StringBuilder clouds = new StringBuilder();
        StringBuilder ids = new StringBuilder();
        for (OpenWeatherMapRs.Weather openWeatherWeather: openWeatherMapRs.getWeather()) {
            clouds.append(clouds.isEmpty() ? "" : ", ")
                    .append(openWeatherWeather.getMain())
                    .append(": ")
                    .append(openWeatherWeather.getDescription());
            ids.append(ids.isEmpty() ? "" : ", ")
                    .append(openWeatherWeather.getId());
        }
        weather.setClouds(clouds.toString());
        weather.setOpenWeatherIds(ids.toString());
        weather.setTemp(openWeatherMapRs.getMain().getTemp());

        weatherRepository.save(weather);

    }

    @DebugLoggable
    private OpenWeatherMapRs getWeather(String city) {
        RestTemplate restTemplate = new RestTemplate();
        try {
            return restTemplate.exchange(WEATHER_URL + "?q=" + city + "&lang=ru&units=metric&appid=" + apiKey,
                    HttpMethod.GET,
                    new HttpEntity<>(new HttpHeaders()),
                    OpenWeatherMapRs.class).getBody();
        } catch (RestClientException e) {
            return null;
        }
    }
}
