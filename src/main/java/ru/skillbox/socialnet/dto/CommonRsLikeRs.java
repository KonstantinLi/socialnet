package ru.skillbox.socialnet.dto;

import lombok.Data;
import ru.skillbox.socialnet.dto.LikeRs;

@Data
public class CommonRsLikeRs {
    private LikeRs data;
    private Integer itemPerPage;
    private Integer offset;
    private Integer perPage;
    private Long timestamp;
    private Long total;
}
