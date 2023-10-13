package ru.skillbox.socialnet.dto.request;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;
import ru.skillbox.socialnet.entity.enums.NotificationType;

@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class PersonSettingsRq {
    private boolean enable;
    private NotificationType notificationType;
}
