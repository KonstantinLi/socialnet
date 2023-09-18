package ru.skillbox.socialnet.dto.request.exception;

import lombok.Data;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

@Data
public class CommonException extends RuntimeException {
    private final HttpStatusCode code;
    private final String error;
    private final HttpStatus httpStatus;
}
