package ru.skillbox.socialnet.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import ru.skillbox.socialnet.dto.response.ErrorRs;

@RequiredArgsConstructor
@Getter
public class InternalServerErrorException extends RuntimeException {
    private final String message;
    private final Exception exception;
}
