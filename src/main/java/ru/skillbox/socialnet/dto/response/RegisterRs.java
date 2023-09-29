package ru.skillbox.socialnet.dto.response;

import lombok.Data;

import java.util.Date;

@Data
public class RegisterRs<T>{

    private T data;
    private String email;
    private Long timestamp = new Date().getTime();
}
