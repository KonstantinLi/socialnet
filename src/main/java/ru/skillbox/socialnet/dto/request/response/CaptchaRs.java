package ru.skillbox.socialnet.dto.request.response;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class CaptchaRs<T> extends ApiFatherRs {

    private String code;
    private String image;


    public CaptchaRs() {
        super();
    }

}
