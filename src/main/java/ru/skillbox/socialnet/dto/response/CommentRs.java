package ru.skillbox.socialnet.dto.response;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

import java.util.Set;

@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)

public class CommentRs {
    private PersonRs author;
    private String commentText;
    private Long id;
    private Boolean isBlocked;
    private Boolean isDeleted;
    private Long likes;
    private Boolean myLike;
    private Long parentId;
    private Long postId;
    private Set<CommentRs> subComments;
    private String time;
}
