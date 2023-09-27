package ru.skillbox.socialnet.exception;

import lombok.Builder;

@Builder
public class AuthException extends RuntimeException {
    public String message;
    public AuthException(String message) {
        super(message);
        this.message = message;
    }

}
