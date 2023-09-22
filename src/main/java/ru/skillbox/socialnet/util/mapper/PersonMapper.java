package ru.skillbox.socialnet.util.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import ru.skillbox.socialnet.dto.request.PersonRs;
import ru.skillbox.socialnet.entity.Person;

@Mapper(componentModel = "spring")
public interface PersonMapper {

    PersonMapper INSTANCE = Mappers.getMapper(PersonMapper.class);

    @Mapping(source = "lastName", target = "last_name")
    @Mapping(source = "firstName", target = "first_name")
    @Mapping(source = "deleted", target = "is_deleted")
    @Mapping(source = "blocked", target = "is_blocked")
    @Mapping(source = "regDate", target = "reg_date")
    @Mapping(source = "birthDate", target = "birth_date")
    PersonRs toRs(Person person);
}
