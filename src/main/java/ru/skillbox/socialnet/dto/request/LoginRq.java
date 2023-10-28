package ru.skillbox.socialnet.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class LoginRq {
    @Schema(description = "user email", example = "obivan.k@remail.com")
    String email;
    @Schema(description = "password", example = "123qwerty")
    String password;
}
