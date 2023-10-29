package ru.skillbox.socialnet.service.weather;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import ru.skillbox.socialnet.dto.response.openweathermap.OpenWeatherMapRs;
import ru.skillbox.socialnet.entity.locationrelated.Weather;
import ru.skillbox.socialnet.repository.WeatherRepository;

import java.time.LocalDateTime;
import java.util.Optional;

public class WeatherCityUpdater extends Thread{

    private final String city;
    private final String apiKey;
    private final WeatherRepository weatherRepository;

    private static final String WEATHER_URL = "https://api.openweathermap.org/data/2.5/weather";

    public WeatherCityUpdater(WeatherRepository weatherRepository, String city, String apiKey) {
        this.city = city;
        this.apiKey = apiKey;
        this.weatherRepository = weatherRepository;
    }

    @Override
    public void run() {
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
