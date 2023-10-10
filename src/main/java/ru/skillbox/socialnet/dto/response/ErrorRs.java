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
        timestamp = new Date().getTime();
    }

    public ErrorRs(String error) {
        this.error = error;
        this.errorDescription = error;
    }

    public ErrorRs (RuntimeException exception) {
        this.error = exception.getClass().getSimpleName();
        this.errorDescription = exception.getLocalizedMessage();
    }

    private String error;
    private Long timestamp = new Date().getTime();
    private String errorDescription;
}
