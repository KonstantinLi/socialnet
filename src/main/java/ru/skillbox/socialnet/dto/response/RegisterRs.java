package ru.skillbox.socialnet.dto.response;

import lombok.Data;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Data
public class RegisterRs<T> {

    private T data;
    private String email;
    private Long timestamp;

    public RegisterRs() {
        this.timestamp = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC);
    }
}
