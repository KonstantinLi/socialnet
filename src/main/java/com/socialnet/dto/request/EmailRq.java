package com.socialnet.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "email set request")
public class EmailRq {
    @Schema(example = "obivan.k@rmail.com")
    private String email;
    @Schema(description = "JWT Token")
    private String secret;
}
