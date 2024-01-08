package com.socialnet.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
@Schema(description = "id's of current user and recipient")
public class DialogUserShortListRq {

    @Schema(description = "current user id", example = "1")
    private Long userId;

    @JsonProperty("user_ids")
    @Schema(description = "recipient", example = "List [ 2, 3, 4 ]")
    private List<Long> userIds;
}
