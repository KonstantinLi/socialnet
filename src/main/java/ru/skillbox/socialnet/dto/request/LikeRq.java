package ru.skillbox.socialnet.dto.request;

import lombok.Data;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import ru.skillbox.socialnet.entity.enums.LikeType;

@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class LikeRq {
    private LikeType type;
    private Integer itemId;
}
