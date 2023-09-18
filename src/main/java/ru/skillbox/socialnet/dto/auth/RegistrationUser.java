package ru.skillbox.socialnet.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegistrationUser {
    private int id;
    private boolean isDeleted;

    @Email(message = "Wrong email format")
    private String email;

    @NotBlank
    @Size(min = 8, message = "Password should have at least 8 characters")
    private String password1;

    @NotBlank(message = "You should confirm your password")
    private String password2;

    @NotBlank(message = "First name should not be empty")
    private String firstName;

    @NotBlank(message = "Last name should not be empty")
    private String lastName;

    @NotBlank(message = "Captcha code should not be empty")
    private String captchaCode;

    @NotBlank
    private String captchaSecret;
}
