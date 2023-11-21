package ru.skillbox.socialnet.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Schema(description = "Request for user registration")
@Data
public class RegisterRq {
    @Schema(description = "entered captcha", example = "zhcyuo")
    private String code;
    @Schema(description = "captcha decryption secret", example = "12345")
    private String codeSecret;
    @Schema(description = "user registration email", example = "obivan.k@remail.com")
    private String email;
    @Schema(pattern = "[А-Яа-яA-Za-z]", description = "first name of a new user", example = "ObiVan")
    private String firstName;
    @Schema(pattern = "[А-Яа-яA-Za-z]", description = "last name of a new user", example = "Kenobi")
    private String lastName;
    @Schema(description = "first password to compare", example = "123qwerty")
    @Size(min = 8, message = "Пароль должен содержать не менее 8 символов")
    private String passwd1;
    @Schema(description = "password repeat to compare", example = "123qwerty")
    @NotNull(message = "Повторите пароль")
    private String passwd2;
}
