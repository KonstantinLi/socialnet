package ru.skillbox.socialnet.errs;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import ru.skillbox.socialnet.dto.response.ErrorRs;

@Getter
@Setter
@RequiredArgsConstructor
public class BadRequestException extends Exception {

    private ErrorRs errorRs;

    public BadRequestException(ErrorRs errorRs) {
        this.errorRs = errorRs;
    }

    public BadRequestException(String message) {
        super(message);
    }
}