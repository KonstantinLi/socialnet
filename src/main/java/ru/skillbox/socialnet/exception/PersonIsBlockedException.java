package ru.skillbox.socialnet.exception;

public class PersonIsBlockedException extends RuntimeException{
    public PersonIsBlockedException(String message) {
        super(message);
    }
}
