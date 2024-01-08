package com.socialnet.dto.response;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;


@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class ComplexRs {
    private Long count;
    @Schema(description = "object id", example = "1")
    private Long id;
    @Schema(example = "sample message")
    private String message;
    @Schema(description = "id message", example = "1")
    private Long messageId;
}