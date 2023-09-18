package ru.skillbox.socialnet.exception;

public class WrongLoginOrPasswordException extends RuntimeException {
    public WrongLoginOrPasswordException() {
        super("Wrong username or password");
    }
}
