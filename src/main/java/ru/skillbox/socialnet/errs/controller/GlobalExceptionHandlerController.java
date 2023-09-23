package ru.skillbox.socialnet.errs.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import ru.skillbox.socialnet.errs.BadRequestException;
import ru.skillbox.socialnet.dto.response.ErrorRs;

@ControllerAdvice
public class GlobalExceptionHandlerController {

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<Object> handleBadRequestException(BadRequestException ex) throws JsonProcessingException {
        ErrorRs rs = new ErrorRs(ex.getClass().getName(), ex.getLocalizedMessage());
        return new ResponseEntity(new ObjectMapper()
                .writerWithDefaultPrettyPrinter()
                .writeValueAsString(rs), HttpStatus.BAD_REQUEST);
    }
}