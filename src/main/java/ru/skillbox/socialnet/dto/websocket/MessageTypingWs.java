package ru.skillbox.socialnet.dto.websocket;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class MessageTypingWs {

    private Boolean typing;

    @JsonProperty("user_id")
    private Long userId;
}