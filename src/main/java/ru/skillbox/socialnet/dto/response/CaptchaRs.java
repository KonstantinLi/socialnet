package ru.skillbox.socialnet.dto.response;

import lombok.Data;

@Data
public class CaptchaRs {

    private String code;
    private String image;

}
