package ru.skillbox.socialnet.dto.parameters;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class GetPostsSearchPs {
    private String author;
    private long dateFrom;
    private long dateTo;
    private String[] tags;
    private String text;
}
