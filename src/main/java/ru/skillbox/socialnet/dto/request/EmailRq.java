package ru.skillbox.socialnet.dto.request;

import lombok.Data;

@Data
public class EmailRq {
    private String email;
    private String secret;
}
