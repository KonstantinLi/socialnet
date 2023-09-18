package ru.skillbox.socialnet.dto.request.response;


import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class ErrorRs extends ApiFatherRs {

    private String error;
    private String error_description;

    public ErrorRs() {
        super();
    }
}
