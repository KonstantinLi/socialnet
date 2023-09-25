package ru.skillbox.socialnet.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class CommentRs {

    private PersonRs author;
    private Long id;
    private Integer like;
    private String time;
    @JsonProperty("comment_text")
    private String commentText;
    @JsonProperty("is_blocked")
    private Boolean isBlocked;
    @JsonProperty("is_deleted")
    private Boolean isDeleted;
    @JsonProperty("my_like")
    private Boolean myLike;
    @JsonProperty("parent_id")
    private Long parentId;
    @JsonProperty("post_id")
    private Long postId;
    @JsonProperty("sub_comments")
    private List<CommentRs> subComments;
}
