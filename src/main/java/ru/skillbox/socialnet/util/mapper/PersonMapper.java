package ru.skillbox.socialnet.util.mapper;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.factory.Mappers;
import ru.skillbox.socialnet.dto.response.PersonRs;
import ru.skillbox.socialnet.entity.personrelated.Person;

@Mapper
public interface PersonMapper {

    PersonMapper INSTANCE = Mappers.getMapper(PersonMapper.class);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(source = "person.id", target = "id")
    @Mapping(source = "person.firstName", target = "first_name")
    @Mapping(source = "person.lastName", target = "last_name")
    @Mapping(source = "person.birthDate", target = "birth_date", dateFormat = "dd.MM.yyyy")
    @Mapping(source = "person.regDate", target = "reg_date", dateFormat = "dd.MM.yyyy")
    @Mapping(source = "person.changePasswordToken", target = "token")
    default Boolean onlineStatusToBoolean(String onlineStatus) {
        return onlineStatus != null && onlineStatus.equalsIgnoreCase("TRUE");
    }


    //TODO Два метода для одной задачи, что делать?
    //TODO isBlockedByCurrentUser нужен?
    @Mapping(source = "person.onlineStatus", target = "online")
    @Mapping(source = "person.messagePermission", target = "messagesPermission")
    @Mapping(source = "person.isBlocked", target = "isBlocked")
    @Mapping(source = "person.isDeleted", target = "userDeleted")
    @Mapping(source = "person.lastOnlineTime", target = "lastOnlineTime", dateFormat = "dd.MM.yyyy hh:mm:ss")
    @Mapping(source = "person.city", target = "city")
    @Mapping(source = "friendStatus", target = "friendStatus")
    @Mapping(source = "isBlockedByCurrentUser", target = "isBlockedByCurrentUser")
    PersonRs personToPersonRs(Person person, String friendStatus, Boolean isBlockedByCurrentUser);

    @Mapping(source = "person.lastName", target = "lastName")
    @Mapping(source = "person.firstName", target = "firstName")
    @Mapping(source = "person.isDeleted", target = "userDeleted")
    @Mapping(source = "person.isBlocked", target = "isBlocked")
    @Mapping(source = "person.regDate", target = "regDate")
    @Mapping(source = "person.birthDate", target = "birthDate")
    @Mapping(source = "person.phone", target = "phone")
    @Mapping(source = "person.photo", target = "photo")
    PersonRs personToPersonRs(Person person);
}
