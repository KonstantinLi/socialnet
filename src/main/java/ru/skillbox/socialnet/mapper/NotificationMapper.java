package ru.skillbox.socialnet.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.factory.Mappers;
import ru.skillbox.socialnet.dto.response.NotificationRs;
import ru.skillbox.socialnet.entity.other.Notification;

@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING,
        uses = {
                PersonMapper.class
        }
)
public interface NotificationMapper {
    NotificationMapper INSTANCE = Mappers.getMapper(NotificationMapper.class);

    @Mapping(target = "info", source = "notification.contact")
    @Mapping(target = "entityAuthor", source = "notification.person")
    NotificationRs notificationToNotificationRs(Notification notification);
}
