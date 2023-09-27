package ru.skillbox.socialnet.exception;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import ru.skillbox.socialnet.dto.response.ErrorRs;

@ControllerAdvice
public class DefaultControllerAdvice {
    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ErrorRs> handleBadRequestException(BadRequestException e) {
        return new ResponseEntity<>(e.getErrorRs(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<?> handleUnauthorizedException(UnauthorizedException e) {
        return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(InternalServerErrorException.class)
    public ResponseEntity<ErrorRs> handleInternalServerErrorException(InternalServerErrorException e) {
        return new ResponseEntity<>(
                new ErrorRs(
                        e.getMessage() + ": " + e.getException().getMessage(),
                        ExceptionUtils.getStackTrace(e.getException())
                ),
                HttpStatus.INTERNAL_SERVER_ERROR
        );
    }
}
