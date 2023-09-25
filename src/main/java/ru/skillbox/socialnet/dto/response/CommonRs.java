package ru.skillbox.socialnet.dto.response;

import lombok.Data;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Data
public class CommonRs<T> {

    private Long timeStamp;
    private T data;
    private Integer itemPerPage;
    private Integer offset;
    private Integer perPage;
    private Long total;

    public CommonRs() {
        this.timeStamp = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC);
    }

}
