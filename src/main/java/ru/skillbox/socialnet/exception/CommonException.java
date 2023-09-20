package ru.skillbox.socialnet.exception;

import lombok.Data;
import org.springframework.http.HttpStatus;

@Data
public class CommonException extends Exception {

    public CommonException(String message) {
        super(message);
    }

}
