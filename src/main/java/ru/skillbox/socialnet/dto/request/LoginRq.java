package ru.skillbox.socialnet.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class LoginRq {
    @Email(message = "Не корректный формат 'email'")
    String email;

    @Size(min = 8, message = "Пароль должен содержать не менее 8 символов")
    String password;
}
