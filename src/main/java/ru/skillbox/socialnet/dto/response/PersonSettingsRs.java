package ru.skillbox.socialnet.dto.response;

import lombok.Data;
import ru.skillbox.socialnet.entity.enums.NotificationType;

@Data
public class PersonSettingsRs {
    private String description;
    private NotificationType type;
    boolean enable;
}
