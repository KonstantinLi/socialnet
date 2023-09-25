package ru.skillbox.socialnet.util.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import ru.skillbox.socialnet.dto.PersonRs;
import ru.skillbox.socialnet.entity.Person;

import java.util.List;

@Mapper(componentModel = "spring")
public interface PersonMapper {

    PersonMapper INSTANCE = Mappers.getMapper(PersonMapper.class);

    @Mapping(source = "lastName", target = "lastName")
    @Mapping(source = "firstName", target = "firstName")
    @Mapping(source = "deleted", target = "userDeleted")
    @Mapping(source = "blocked", target = "isBlocked")
    @Mapping(source = "regDate", target = "regDate")
    @Mapping(source = "birthDate", target = "birthDate")
    PersonRs toRs(Person person);

    List<PersonRs> toRsList(List<Person> personList);
}
