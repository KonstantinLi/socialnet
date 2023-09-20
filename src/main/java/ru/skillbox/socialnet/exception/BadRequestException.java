package ru.skillbox.socialnet.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import ru.skillbox.socialnet.dto.response.ErrorRs;

@RequiredArgsConstructor
@Getter
public class BadRequestException extends RuntimeException {
    private final ErrorRs errorRs;

    public BadRequestException(String message, String description) {
        errorRs = new ErrorRs(message, description);
    }

    public BadRequestException(String message) {
        errorRs = new ErrorRs(message);
    }
}