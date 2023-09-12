package ru.skillbox.socialnet.dto;

import lombok.Data;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;

@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class CommentRq {
    private String commentText;
    private Integer parentId;
}
