package ru.skillbox.socialnet.errs.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import ru.skillbox.socialnet.errs.BadRequestException;
import ru.skillbox.socialnet.dto.response.ErrorRs;

@ControllerAdvice
public class GlobalExceptionHandlerController {

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ErrorRs> handleBadRequestException(Exception exception) {
        return new ResponseEntity<>(new ErrorRs(exception.getClass().getSimpleName(),
                exception.getLocalizedMessage()),
                HttpStatus.BAD_REQUEST);
    }
}
