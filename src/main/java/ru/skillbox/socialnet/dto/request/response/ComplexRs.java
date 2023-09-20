package ru.skillbox.socialnet.dto.request.response;

import lombok.Data;
import lombok.EqualsAndHashCode;


@EqualsAndHashCode(callSuper = true)
@Data
public class ComplexRs extends ApiFatherRs {

    private Integer count;
    private Integer id;
    private String message;
    private Integer message_id;

    public ComplexRs() {
        super();
    }

}