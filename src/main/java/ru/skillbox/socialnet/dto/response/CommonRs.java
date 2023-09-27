package ru.skillbox.socialnet.dto.response;

import lombok.Data;

import java.util.Date;

@Data
public class CommonRs<T> {
    private T data;
    private Integer itemPerPage;
    private Integer offset;
    private Integer perPage;
    private Long total;
    private Long timestamp = new Date().getTime();
}
