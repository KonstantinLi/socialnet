package ru.skillbox.socialnet.exception;

public class AuthException extends BadRequestException {
    public AuthException(String message) {
        super(message);
    }

    public static AuthException userNotFoundByEmail(String email) {
        return new AuthException(String.format("Не найден пользователь с e-mail %s", email));
    }

    public static AuthException incorrectPassword() {
        return new AuthException("Некорректный пароль");
    }

    public static AuthException userIsBlocked() {
        return new AuthException("Пользователь заблокирован");
    }
}
