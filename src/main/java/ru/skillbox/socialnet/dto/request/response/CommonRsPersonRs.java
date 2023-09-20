package ru.skillbox.socialnet.dto.request.response;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Collection;

@EqualsAndHashCode(callSuper = true)
@Data
public class CommonRsPersonRs<T> extends ApiFatherRs {
    private T data;
    private Integer itemPerPage;
    private Integer offset;
    private Integer perPage;
    private Long timestamp;
    private Integer total;


    public CommonRsPersonRs() {
        super();
    }

}
