package com.socialnet.dto.request;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;
import com.socialnet.entity.enums.LikeType;

@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class LikeRq {
    private LikeType type;
    private Long itemId;
}
