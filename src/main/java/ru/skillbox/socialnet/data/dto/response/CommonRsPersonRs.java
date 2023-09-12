package ru.skillbox.socialnet.data.dto.response;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Collection;

@EqualsAndHashCode(callSuper = true)
@Data
public class CommonRsPersonRs<T> extends ApiFatherRs {

    private Collection<T> data;
    private Integer itemPerPage;
    private Integer offset;
    private Integer perPage;
    private Long total;


    public CommonRsPersonRs() {
        super();
    }

}
