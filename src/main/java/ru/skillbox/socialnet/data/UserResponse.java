package ru.skillbox.socialnet.data;

import lombok.Data;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Collection;

@Data
public class UserResponse<T> {

    private long timeStamp;
    private Collection<T> data;

    public UserResponse() {
        this.timeStamp = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC);
    }

}
