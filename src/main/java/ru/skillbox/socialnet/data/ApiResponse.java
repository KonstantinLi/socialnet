package ru.skillbox.socialnet.data;

import lombok.Getter;
import lombok.Setter;
import ru.skillbox.socialnet.data.dto.UserDto;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Collection;

@Getter
@Setter
public class ApiResponse<T> {

    private long timeStamp;
    private Collection<T> data;

    public ApiResponse() {
        this.timeStamp = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC);
    }

    public ApiResponse(UserDto userDto) {
        this();
        this.data.add((T) userDto);
    }

}
