package ru.skillbox.socialnet.exception;

import lombok.Data;

@Data
public class ExceptionBadRq extends Exception {

    public ExceptionBadRq(String message) {
        super(message);
    }

}
