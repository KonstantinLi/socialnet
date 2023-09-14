package ru.skillbox.socialnet.dto.response;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

import java.util.List;

@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class PostRs {
    private PersonRs author;
    private CommentRs comments;
    private Long id;
    private Integer likes;
    private List<String> tags;
    private String time;
    private String title;
    private String type;
    private boolean isBlocked;
    private boolean myLike;
    private String postText;
}
