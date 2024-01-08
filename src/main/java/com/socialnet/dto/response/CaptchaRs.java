package com.socialnet.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class CaptchaRs {

    @Schema(example = "12345")
    private String code;

    @Schema(example = "/some/path")
    private String image;
}
