package ru.skillbox.socialnet.dto.response;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

import java.util.Set;

@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class PostRs {
    private PersonRs author;
    private Set<CommentRs> comments;
    private Long id;
    private Integer likes;
    private Set<String> tags;
    private String time;
    private String title;
    private String type;
    private boolean isBlocked;
    private Boolean myLike;
    private String postText;
}
