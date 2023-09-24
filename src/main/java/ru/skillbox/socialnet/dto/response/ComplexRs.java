package ru.skillbox.socialnet.dto.response;

import lombok.Data;


@Data
public class ComplexRs {

    private Integer count;
    private Integer id;
    private String message;
    private Integer message_id;

    public ComplexRs() {
        super();
    }

}