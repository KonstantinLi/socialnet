package com.socialnet.dto.response;

import lombok.Data;
import com.socialnet.entity.enums.NotificationType;

@Data
public class PersonSettingsRs {
    private String description;
    private NotificationType type;
    boolean enable;
}
