package ru.skillbox.socialnet.dto.request;

import lombok.Data;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;

import java.util.Set;

@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class PostRq {
    private Set<String> tags;
    private String title;
    private String postText;

    public Boolean isDeleted;
}
