package ru.skillbox.socialnet.dto.response;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

import java.util.List;

@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class CommentRs {
    private PersonRs author;
    private String commentText;
    private Long id;
    private Boolean isBlocked;
    private Boolean isDeleted;
    private Integer likes;
    private Boolean myLike;
    private Long parentId;
    private Long postId;
    private List<CommentRs> subComments;
    private String time;
}
