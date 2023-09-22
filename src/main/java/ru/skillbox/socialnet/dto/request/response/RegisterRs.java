package ru.skillbox.socialnet.dto.request.response;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
public class RegisterRs<T>{

    private T data;
    private String email;
    private Long timestamp;


    public RegisterRs() {
        super();
    }

}
