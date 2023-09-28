package ru.skillbox.socialnet.exception;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AuthException extends RuntimeException {
    public AuthException(String message) {
        super(message);
    }
}
