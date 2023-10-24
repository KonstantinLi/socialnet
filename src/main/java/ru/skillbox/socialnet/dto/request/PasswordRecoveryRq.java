package ru.skillbox.socialnet.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class PasswordRecoveryRq {
    @Schema(description = "email for password recovery", example = "fullName@gamil.com")
    private String email;
}
