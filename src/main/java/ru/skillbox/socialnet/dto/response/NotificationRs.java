package ru.skillbox.socialnet.dto.response;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import ru.skillbox.socialnet.entity.enums.NotificationType;

@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class NotificationRs {
    @Schema(example = "1", description = "id of notification")
    private Integer id;

    @Schema(example = "some info", description = "info about notification")
    private String info;

    @Schema(implementation = PersonRs.class, description = "The data we are looking for")
    private PersonRs entityAuthor;

    @Schema(implementation = NotificationType.class, description = "notification type Enum: [ COMMENT_COMMENT, FRIEND_BIRTHDAY, FRIEND_REQUEST, MESSAGE, POST, POST_COMMENT, POST_LIKE ]")
    private NotificationType notificationType;

    @Schema(example = "2011-12-03T10:15:30", description = "when notification sent")
    private String sentTime;
}
