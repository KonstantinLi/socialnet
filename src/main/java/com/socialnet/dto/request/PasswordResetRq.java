package com.socialnet.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class PasswordResetRq {
    @Schema(description = "new password", example = "123qwerty")
    private String password;
    @Schema(description = "authorization token", example = "JWT Token")
    private String secret;
}
