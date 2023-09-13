package ru.skillbox.socialnet.data.dto.response;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Collection;

@EqualsAndHashCode(callSuper = true)
@Data
public class CommonRsComplexRs<ComplexRs> extends ApiFatherRs {

    private Collection<ComplexRs> data;
    private Integer itemPerPage;
    private Integer offset;
    private Integer perPage;
    private Long total;


    public CommonRsComplexRs() {
        super();
    }
}
