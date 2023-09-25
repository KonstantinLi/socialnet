package ru.skillbox.socialnet.dto.request.response;


import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
public class ErrorRs {

    private String error;
    private String error_description;

    public ErrorRs() {
        super();
    }
}
