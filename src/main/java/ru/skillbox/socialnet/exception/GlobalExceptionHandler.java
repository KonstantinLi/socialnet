package ru.skillbox.socialnet.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import ru.skillbox.socialnet.dto.response.ErrorRs;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler({CommonException.class})
    public ResponseEntity<ErrorRs> handleCommonExceptions(CommonException exception) {

        return ResponseEntity.badRequest().body(new ErrorRs(exception));
    }
}