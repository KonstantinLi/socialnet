package ru.skillbox.socialnet.dto.response;

import lombok.Data;

@Data
public class RegisterRs<T>{

    private T data;
    private String email;
    private Long timestamp;
}
