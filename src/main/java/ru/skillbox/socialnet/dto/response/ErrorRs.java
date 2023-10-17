package ru.skillbox.socialnet.dto.response;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;

@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class ErrorRs {

    @Schema(example = "PersonNotFoundException")
    private String error;
    @Schema(example = "12432857239")
    private Long timestamp;
    @Schema(example = "Запись о профиле не найдена")
    private String errorDescription;

    public ErrorRs(RuntimeException exception) {
        this.error = exception.getClass().getSimpleName();
        this.errorDescription = exception.getLocalizedMessage();
        this.timestamp = new Date().getTime();
    }
}
