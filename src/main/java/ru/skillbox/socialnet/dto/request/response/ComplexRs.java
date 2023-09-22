package ru.skillbox.socialnet.dto.request.response;

import lombok.Data;
import lombok.EqualsAndHashCode;


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