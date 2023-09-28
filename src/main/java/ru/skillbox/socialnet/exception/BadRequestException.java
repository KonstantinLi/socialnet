package ru.skillbox.socialnet.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
public class BadRequestException extends RuntimeException {
    public BadRequestException(String message) {
        super(message);
    }
}