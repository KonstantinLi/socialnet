package ru.skillbox.socialnet.dto.request.response;

import lombok.Data;

@Data
public class CaptchaRs {

    private String code;
    private String image;

}
