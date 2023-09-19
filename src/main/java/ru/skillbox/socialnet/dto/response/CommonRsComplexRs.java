package ru.skillbox.socialnet.dto.response;

import lombok.Data;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Collection;

@Data
public class CommonRsComplexRs<ComplexRs> {

    private Collection<ComplexRs> data;
    private Integer itemPerPage;
    private Integer offset;
    private Integer perPage;
    private Long total;
    private Long timeStamp;


    public CommonRsComplexRs() {
        this.timeStamp = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC);
    }
}
