package ru.skillbox.socialnet.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Date;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class ErrorRs {

    private Long timeStamp;
    private String error;
    private String errorDescription;

    public ErrorRs(String error, String errorDescription) {
        this.timeStamp = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC);
        this.error = error;
        this.errorDescription = errorDescription;
    }

    public ErrorRs(String error) {
        this.error = error;
        this.errorDescription = error;
        this.timeStamp = new Date().getTime();
    }
}
