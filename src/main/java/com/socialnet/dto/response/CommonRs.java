package com.socialnet.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CommonRs<T> {

    @Schema(example = "12432857239")
    private Long timeStamp;
    @Schema(example = "Collection of objects or just object any type")
    private T data;
    @Schema(example = "20")
    private Integer itemPerPage;
    @Schema(example = "0")
    private Integer offset;
    @Schema(example = "20")
    private Integer perPage;
    @Schema(example = "500")
    private Long total;

    public CommonRs() {
        this.timeStamp = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC);
    }

}
