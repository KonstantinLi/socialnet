package ru.skillbox.socialnet.exception.old;

import lombok.Data;

@Data
public class ExceptionBadRq extends Exception {

    public ExceptionBadRq(String message) {
        super(message);
    }

}
