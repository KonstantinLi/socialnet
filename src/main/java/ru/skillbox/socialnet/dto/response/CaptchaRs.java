package ru.skillbox.socialnet.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class CaptchaRs {

    @Schema(example = "78569")
    private String code;

    @Schema(example = "/some/path")
    private String image;
}
