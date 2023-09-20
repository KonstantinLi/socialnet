package ru.skillbox.socialnet.dto.response;

import lombok.Data;

import java.util.List;

@Data
public class CommonRsListCommentRs {
    private List<CommentRs> data;
    private Integer itemPerPage;
    private Integer offset;
    private Integer perPage;
    private Long timestamp;
    private Long total;
}
