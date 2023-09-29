package ru.skillbox.socialnet.dto.response;


import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorRs {

    private Long timeStamp;
    private String error;
    @JsonProperty("error_description")
    private String errorDescription;

    public ErrorRs(String error, String errorDescription) {
        this.timeStamp = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC);
        this.error = error;
        this.errorDescription = errorDescription;
    }
}
