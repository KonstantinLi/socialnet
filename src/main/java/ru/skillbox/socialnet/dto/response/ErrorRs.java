package ru.skillbox.socialnet.dto.response;


import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

@EqualsAndHashCode(callSuper = true)
@Data
public class ErrorRs extends ApiFatherRs {

    private String error;
    private String error_description;
    private Long timeStamp;

    public ErrorRs() {
        this.timeStamp = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC);
    }
}
