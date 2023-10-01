package ru.skillbox.socialnet.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import ru.skillbox.socialnet.entity.enums.PostType;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PostRs {

    private PersonRs author;
    private CommentRs comment;
    private Long id;
    private Integer likes;
    private String tags;
    private String time;
    private String title;
    private PostType type;
    @JsonProperty("is_blocked")
    private Boolean isBlocked;
    @JsonProperty("my_like")
    private Boolean myLike;
    @JsonProperty("post_text")
    private String postText;

}
