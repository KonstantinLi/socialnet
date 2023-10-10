package ru.skillbox.socialnet.dto.request;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

import java.util.Set;

@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class PostRq {
    private Set<String> tags;
    private String title;
    private String postText;

    public Boolean isDeleted;
}
