package ru.skillbox.socialnet.dto.response;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class CommonRsPersonRs<T>{

    private T data;
    private Integer itemPerPage;
    private Integer offset;
    private Integer perPage;
    private Long total;
    private Long timeStamp;

}
