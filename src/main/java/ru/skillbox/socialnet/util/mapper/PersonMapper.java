package ru.skillbox.socialnet.util.mapper;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.factory.Mappers;

import ru.skillbox.socialnet.dto.response.PersonRs;
import ru.skillbox.socialnet.entity.Person;

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
        return  onlineStatus==null ? false :  onlineStatus.equalsIgnoreCase("TRUE");
    }
    @Mapping(source = "person.onlineStatus", target = "online")
    @Mapping(source ="person.messagePermission", target = "messages_permission")
    @Mapping(source = "person.isBlocked", target = "is_blocked")
    @Mapping(source = "person.isDeleted", target = "is_deleted")
    @Mapping(source = "person.lastOnlineTime", target = "last_online_time", dateFormat = "dd.MM.yyyy hh:mm:ss")
    @Mapping(source = "person.city", target = "city")
    @Mapping(source = "friendStatus", target = "friend_status")
    @Mapping(source = " isBlockedByCurrentUser", target = "is_blocked_by_current_user")
    PersonRs personToPersonRs(Person person, String friendStatus, Boolean  isBlockedByCurrentUser);
}
