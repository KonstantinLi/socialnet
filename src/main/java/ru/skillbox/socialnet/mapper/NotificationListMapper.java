package ru.skillbox.socialnet.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
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
    List<NotificationRs> toNotificationRsList(List<Notification> notifications);
}
