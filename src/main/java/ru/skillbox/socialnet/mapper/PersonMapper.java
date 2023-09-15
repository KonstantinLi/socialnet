package ru.skillbox.socialnet.mapper;

import org.mapstruct.Mapper;

import ru.skillbox.socialnet.dto.response.PersonRs;

import ru.skillbox.socialnet.entity.Person;

@Mapper
public interface PersonMapper {
    PersonRs personToPersonRs(Person person);
}
