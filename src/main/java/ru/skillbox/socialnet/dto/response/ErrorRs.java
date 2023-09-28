package ru.skillbox.socialnet.dto.response;


import lombok.Data;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Data
public class ErrorRs {

    private String error;
    private String errorDescription;
    private Long timeStamp;

    public ErrorRs(String error, String errorDescription) {
        this.error = error;
        this.errorDescription = errorDescription;
        this.timeStamp = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC);
    }

    public ErrorRs (RuntimeException exception) {
        this.error = exception.getClass().getSimpleName();
        this.errorDescription = exception.getMessage();
        this.timeStamp = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC);
    }
}
