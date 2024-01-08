package com.socialnet.mapper;

import com.socialnet.dto.response.NotificationRs;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import com.socialnet.entity.other.Notification;

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
