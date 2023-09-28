package ru.skillbox.socialnet.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import ru.skillbox.socialnet.dto.response.ErrorRs;

@ControllerAdvice
public class ErrorHandler {

//    @ExceptionHandler(CommonException.class)
//    public ResponseEntity<ErrorResponse> handleCommonException(CommonException ex) {
//        ex.printStackTrace();
//        ErrorResponse errorResponse = new ErrorResponse();
//        errorResponse.setError(ex.getMessage());
//        errorResponse.setError_description(ex.getMessage());
//        return new ResponseEntity<>(errorResponse);
//    }

    @ExceptionHandler(ExceptionBadRq.class)
    public ResponseEntity<ErrorRs> handle(Exception exception) {
        ErrorRs errorRs = new ErrorRs("", exception.getLocalizedMessage());
        ResponseEntity responseEntity = new ResponseEntity<>(errorRs, HttpStatus.BAD_REQUEST);
        return responseEntity;
    }

}
