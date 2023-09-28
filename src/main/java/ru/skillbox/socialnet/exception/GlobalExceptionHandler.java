package ru.skillbox.socialnet.exception;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import ru.skillbox.socialnet.dto.response.ErrorRs;

//TODO Добавить все исключения в обработчик, сделать одним методом?
//TODO возвращаемое значение должно быть ErrorRs?
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler({BadRequestException.class})
    public ResponseEntity<Object> handleBadRequestException(BadRequestException ex) throws JsonProcessingException {
        ErrorRs rs = new ErrorRs(ex.getClass().getName(), ex.getLocalizedMessage());
        return new ResponseEntity(new ObjectMapper()
                .writerWithDefaultPrettyPrinter()
                .writeValueAsString(rs), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({AuthException.class})
    public ResponseEntity<Object> handleAuthException(AuthException ex) throws JsonProcessingException {
        ErrorRs rs = new ErrorRs(ex.getClass().getName(), ex.getLocalizedMessage());
        return new ResponseEntity(new ObjectMapper()
                .writerWithDefaultPrettyPrinter()
                .writeValueAsString(rs), HttpStatus.BAD_REQUEST);
    }
}