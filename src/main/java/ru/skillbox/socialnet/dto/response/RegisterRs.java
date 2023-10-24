package ru.skillbox.socialnet.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RegisterRs<T> {

    @Schema(example = "collection of objects or just object any type")
    private T data;
    @Schema(example = "obivan.k@remail.com")
    private String email;
    @Schema(example = "12432857239")
    private Long timestamp;

    public RegisterRs() {
        this.timestamp = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC);
    }
}
