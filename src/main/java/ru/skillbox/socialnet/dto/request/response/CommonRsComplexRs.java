package ru.skillbox.socialnet.dto.request.response;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Collection;

@Data
public class CommonRsComplexRs<T>{

    private T data;
    private Integer itemPerPage;
    private Integer offset;
    private Integer perPage;
    private Integer total;
    private Long timestamp;


    public CommonRsComplexRs() {
        super();
    }

}
