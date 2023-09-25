package ru.skillbox.socialnet.dto.request.response;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Collection;

@Data
public class CommonRsPersonRs<T>{
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
