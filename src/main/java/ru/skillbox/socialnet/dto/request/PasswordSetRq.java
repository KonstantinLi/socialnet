package ru.skillbox.socialnet.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class PasswordSetRq {
    @Schema(description = "new password", example = "123qwerty")
    private String password;
}
