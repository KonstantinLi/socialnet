package ru.skillbox.socialnet.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class InternalServerErrorException extends RuntimeException {
    private final String message;
    private final Exception exception;
}
