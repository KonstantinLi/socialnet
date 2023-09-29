package ru.skillbox.socialnet.exception.old;

import ru.skillbox.socialnet.dto.response.ErrorRs;
import ru.skillbox.socialnet.exception.old.BadRequestException;

public class NoRecordFoundException extends BadRequestException {
    public static final String ERROR_NO_RECORD_FOUND = "No record found";

    public NoRecordFoundException(ErrorRs errorRs) {
        super(errorRs);
    }

    public NoRecordFoundException(String description) {
        super(new ErrorRs(ERROR_NO_RECORD_FOUND, description));
    }
}
