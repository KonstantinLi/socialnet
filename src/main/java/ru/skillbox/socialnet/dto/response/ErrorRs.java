package ru.skillbox.socialnet.dto.response;


import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class ErrorRs extends ApiFatherRs {

    private String error;
    private String error_description;

    public ErrorRs(String error, String error_description) {
        super();
        this.error = error;
        this.error_description = error_description;
    }
}
