package ru.skillbox.socialnet.dto.response;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Collection;

@Data
public class CommonRsListPersonRs<PersonRs>{
    private Collection<PersonRs> data;
    private Integer itemPerPage;
    private Integer offset;
    private Integer perPage;
    private Long total;
    private Long timeStamp;

    public CommonRsListPersonRs() {
        this.timeStamp = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC);
    }
}
