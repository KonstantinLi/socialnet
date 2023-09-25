package ru.skillbox.socialnet.dto.response;


import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Data
@NoArgsConstructor
public class ErrorRs {

    private Long timeStamp;
    private String error;
    private String error_description;

    public ErrorRs(String error, String error_description) {
        this.timeStamp = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC);
        this.error = error;
        this.error_description = error_description;
    }
}
