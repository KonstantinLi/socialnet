package ru.skillbox.socialnet.data.dto.response;

import lombok.Data;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Data
public class ApiResponse {

    private Long timeStamp;

    public ApiResponse() {
        this.timeStamp = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC);
    }
}
