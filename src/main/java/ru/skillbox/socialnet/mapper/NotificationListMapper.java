package ru.skillbox.socialnet.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.factory.Mappers;
import ru.skillbox.socialnet.dto.response.NotificationRs;
import ru.skillbox.socialnet.entity.other.Notification;

import java.util.List;

@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING,
        uses = {
                NotificationMapper.class
        }
)
public interface NotificationListMapper {
    NotificationListMapper INSTANCE = Mappers.getMapper(NotificationListMapper.class);

    List<NotificationRs> toNotificationRsList(List<Notification> notifications);
}
