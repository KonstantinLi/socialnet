package ru.skillbox.socialnet.errs.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.skillbox.socialnet.errs.BadRequestException;
import ru.skillbox.socialnet.dto.response.ErrorRs;

@ControllerAdvice
public class GlobalExceptionHandlerController {

    @ExceptionHandler(BadRequestException.class)
    @ResponseStatus( value = HttpStatus.BAD_REQUEST)
    public ErrorRs handleBadRequestException(Exception e) {
        ErrorRs rs = new ErrorRs("Error", e.getLocalizedMessage());
        return rs;
    }
}
