package ru.skillbox.socialnet.dto.request;

import lombok.Data;

@Data
public class PasswordRq {
    private String password;
    private String secret;
}
