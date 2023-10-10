package ru.skillbox.socialnet.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.Data;

@Data
public class DialogUserShortListRq {

  private Long userId;

  @JsonProperty("user_ids")
  private List<Long> userIds;
}
