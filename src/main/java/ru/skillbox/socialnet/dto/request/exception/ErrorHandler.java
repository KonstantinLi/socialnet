package ru.skillbox.socialnet.dto.request.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import ru.skillbox.socialnet.dto.request.response.ErrorRs;

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

    @ExceptionHandler(CommonException.class)
    public ResponseEntity<ErrorRs> handleUnexpectedErrorException(CommonException ex) {
        ex.printStackTrace();
        ErrorRs errorResponse = new ErrorRs();
        errorResponse.setError(ex.getMessage());
        errorResponse.setError_description(ex.getMessage());
        return null;
//                new ResponseEntity<Response>(errorResponse);
    }
}
