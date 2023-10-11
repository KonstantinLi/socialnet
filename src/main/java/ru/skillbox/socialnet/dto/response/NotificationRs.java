package ru.skillbox.socialnet.dto.response;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;
import ru.skillbox.socialnet.entity.enums.NotificationType;

@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class NotificationRs {
    private Integer id;
    private String info;
    private PersonRs entityAuthor;
    private NotificationType notificationType;
    private String sentTime;
}
