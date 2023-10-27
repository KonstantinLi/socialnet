package ru.skillbox.socialnet.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "password recovery request")
public class PasswordRecoveryRq {
    @Schema(description = "email for password recovery", example = "obivan.k@rmail.com")
    private String email;
}
