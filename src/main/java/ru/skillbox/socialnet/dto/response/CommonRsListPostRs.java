package ru.skillbox.socialnet.dto.response;

import lombok.Data;

import java.util.List;

@Data
public class CommonRsListPostRs {
    private List<PostRs> data;
    private Integer itemPerPage;
    private Integer offset;
    private Integer perPage;
    private Long timestamp;
    private Long total;
}
