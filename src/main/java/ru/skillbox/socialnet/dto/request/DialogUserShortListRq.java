package ru.skillbox.socialnet.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class DialogUserShortListRq {

    private Long userId;

    @JsonProperty("user_ids")
    private List<Long> userIds;
}
