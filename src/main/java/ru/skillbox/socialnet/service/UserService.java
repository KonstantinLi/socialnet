package ru.skillbox.socialnet.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.skillbox.socialnet.dto.PersonRs;
import ru.skillbox.socialnet.entity.Person;
import ru.skillbox.socialnet.repository.PersonRepository;
import ru.skillbox.socialnet.repository.WeatherRepository;
import ru.skillbox.socialnet.util.mapper.PersonMapper;
import ru.skillbox.socialnet.util.mapper.WeatherMapper;

import java.util.Optional;

@Service
public class UserService {

    private final PersonRepository personRepository;
    private final WeatherRepository weatherRepository;
    private final PersonMapper personMapper;
    private final WeatherMapper weatherMapper;

    @Autowired
    public UserService(PersonRepository personRepository,
                       WeatherRepository weatherRepository,
                       PersonMapper personMapper,
                       WeatherMapper weatherMapper) {
        this.personRepository = personRepository;
        this.weatherRepository = weatherRepository;
        this.personMapper = personMapper;
        this.weatherMapper = weatherMapper;
    }

    public PersonRs getUserById(Long userId) {
        Optional<Person> optional = personRepository.findById(userId);
        //return optional.map(personMapper::toRs).orElse(null);
        if (optional.isPresent()) {
            Person person = optional.get();
            PersonRs personRs = personMapper.toRs(person);
            personRs.setWeather(weatherMapper.toRs(weatherRepository.findLastByCity(person.getCity())));
            return personRs;
        }
        return null;
    }
}
