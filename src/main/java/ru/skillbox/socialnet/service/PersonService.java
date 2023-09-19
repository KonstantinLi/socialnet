package ru.skillbox.socialnet.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.skillbox.socialnet.dto.PersonRs;
import ru.skillbox.socialnet.dto.errs.BadRequestException;
import ru.skillbox.socialnet.entity.Person;
import ru.skillbox.socialnet.repository.PersonRepository;
import ru.skillbox.socialnet.repository.WeatherRepository;
import ru.skillbox.socialnet.util.mapper.PersonMapper;
import ru.skillbox.socialnet.util.mapper.WeatherMapper;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PersonService {

    private final PersonRepository personRepository;
    private final WeatherRepository weatherRepository;
    private final PersonMapper personMapper;
    private final WeatherMapper weatherMapper;

    public PersonRs getUserById(Long otherUserId, Long currentUserId) throws BadRequestException {
        Optional<Person> optional = personRepository.findById(otherUserId);
        if (optional.isEmpty()) {
            throw new BadRequestException("no such person on id");
        }
        Person person = optional.get();
        PersonRs personRs = personMapper.toRs(person);
        personRs.setWeather(weatherMapper.toRs(weatherRepository.findLastByCity(person.getCity())));

        return personRs;
    }
}
