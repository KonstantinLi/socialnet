package ru.skillbox.socialnet.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class LoginUser {
    @Email(message = "Wrong email format")
    private String email;

    @Size(min = 8, message = "Password should have at least 8 characters")
    private String password;
}
