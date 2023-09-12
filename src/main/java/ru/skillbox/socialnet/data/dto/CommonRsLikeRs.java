package ru.skillbox.socialnet.data.dto;

import lombok.Data;

@Data
public class CommonRsLikeRs {
    private LikeRs data;
    private Integer itemPerPage;
    private Integer offset;
    private Integer perPage;
    private Long timestamp;
    private Long total;
}
