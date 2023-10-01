package ru.skillbox.socialnet.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import ru.skillbox.socialnet.dto.response.ErrorRs;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler({BadRequestException.class})
    public ResponseEntity<ErrorRs> handleCommonExceptions(BadRequestException exception) {

        return ResponseEntity.badRequest().body(new ErrorRs(exception));
    }
}