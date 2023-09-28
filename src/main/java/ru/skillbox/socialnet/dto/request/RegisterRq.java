package ru.skillbox.socialnet.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class RegisterRq {

    @NotNull(message = "Введите код с картинки")
    private String code;
    private String codeSecret;

    @Email(message = "Не корректный формат 'email'")
    private String email;

    @NotNull(message = "Введите имя")
    private String firstName;
    private String lastName;

    //TODO комментарий с кодом?
    //     @Size(min = 8, message = "Password should have at least 8 characters")
    @NotNull(message = "Введите пароль")
    private String passwd1;

    @NotNull(message = "Повторите пароль")
    private String passwd2;
}

