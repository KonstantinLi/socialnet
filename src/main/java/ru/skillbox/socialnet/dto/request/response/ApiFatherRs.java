package ru.skillbox.socialnet.dto.request.response;

import lombok.Data;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Data
public class ApiFatherRs {

    private Long timeStamp;

    public ApiFatherRs() {
        this.timeStamp = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC);
    }
}
