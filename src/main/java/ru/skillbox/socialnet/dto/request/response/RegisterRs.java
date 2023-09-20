package ru.skillbox.socialnet.dto.request.response;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class RegisterRs<T> extends  ApiFatherRs{

    private T data;
    private String email;
    private Long timestamp;


    public RegisterRs() {
        super();
    }

}
