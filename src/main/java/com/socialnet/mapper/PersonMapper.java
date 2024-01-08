package com.socialnet.mapper;

import com.socialnet.dto.response.PersonRs;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.factory.Mappers;
import com.socialnet.entity.personrelated.Person;

import java.util.List;

@SuppressWarnings("Annotator")
@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING
)
public interface PersonMapper {

    PersonMapper INSTANCE = Mappers.getMapper(PersonMapper.class);

    @Mapping(target = "online", source = "onlineStatus")
    @Mapping(target = "userDeleted", source = "isDeleted")
    @Mapping(target = "messagesPermission", source = "person.messagePermissions")
    PersonRs personToPersonRs(Person person);

    @Mapping(target = "online", source = "person.onlineStatus")
    @Mapping(target = "userDeleted", source = "person.isDeleted")
    @Mapping(target = "messagesPermission", source = "person.messagePermissions")
    @Mapping(target = "friendStatus", source = "friendStatus")
    @Mapping(target = "isBlockedByCurrentUser", source = " isBlockedByCurrentUser")
    PersonRs personToPersonRs(Person person, String friendStatus, Boolean isBlockedByCurrentUser);

    List<PersonRs> toRsList(List<Person> personList);
}
