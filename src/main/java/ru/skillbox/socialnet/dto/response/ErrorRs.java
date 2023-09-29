package ru.skillbox.socialnet.dto.response;

import lombok.Data;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;

import java.util.Date;

@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class ErrorRs {
    public ErrorRs(String error, String errorDescription) {
        this.error = error;
        this.errorDescription = errorDescription;
    }

    public ErrorRs(String error) {
        this.error = error;
        this.errorDescription = error;
    }

    private String error;
    private Long timestamp = new Date().getTime();
    private String errorDescription;
}
