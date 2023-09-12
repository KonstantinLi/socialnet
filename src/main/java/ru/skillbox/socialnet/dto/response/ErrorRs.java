package ru.skillbox.socialnet.dto.response;

import lombok.Data;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;

@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class ErrorRs {
    private String error;
    private Long timestamp;
    private String errorDescription;
}
