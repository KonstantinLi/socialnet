package ru.skillbox.socialnet.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import ru.skillbox.socialnet.dto.response.PersonRs;
import ru.skillbox.socialnet.entity.Person;

@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING
)
public interface PersonMapper {
    @Mapping(target = "online", source = "onlineStatus")
    @Mapping(target = "userDeleted", source = "isDeleted")
    PersonRs personToPersonRs(Person person);
}
