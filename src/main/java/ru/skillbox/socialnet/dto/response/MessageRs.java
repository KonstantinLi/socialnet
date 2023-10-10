package ru.skillbox.socialnet.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDateTime;
import lombok.Data;
import ru.skillbox.socialnet.entity.enums.ReadStatus;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MessageRs {

  private Long id;
  private Boolean isSentByMe;
  private PersonRs recipient;

  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
  private LocalDateTime time;

  @JsonProperty("author_id")
  private Long authorId;

  @JsonProperty("message_text")
  private String messageText ;

  @JsonProperty("read_status")
  private ReadStatus readStatus;

  @JsonProperty("recipient_id")
  private Long recipientId;

}

