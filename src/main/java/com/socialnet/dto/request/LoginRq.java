package com.socialnet.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class LoginRq {
    @Schema(description = "user email", example = "obivan.k@remail.com")
    @Email(message = "Не корректный формат 'email'")
    String email;
    @Schema(description = "password", example = "123qwerty")
    @Size(min = 8, message = "Пароль должен содержать не менее 8 символов")
    String password;
}
