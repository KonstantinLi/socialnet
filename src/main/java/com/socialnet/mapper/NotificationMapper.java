package com.socialnet.mapper;

import com.socialnet.dto.response.NotificationRs;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import com.socialnet.entity.other.Notification;

@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING,
        uses = {
                PersonMapper.class
        }
)
public interface NotificationMapper {
    @Mapping(target = "info", source = "notification.contact")
    @Mapping(target = "entityAuthor", source = "notification.sender")
    NotificationRs notificationToNotificationRs(Notification notification);
}
