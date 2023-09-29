package ru.skillbox.socialnet.util;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.skillbox.socialnet.exception.old.ExceptionBadRq;
import ru.skillbox.socialnet.exception.old.ErrorHandler;

@Service
@RequiredArgsConstructor
public class ValidationUtilsRq extends RuntimeException {
    private final ErrorHandler errorHandler;

    public <T> void validationRegPassword(String password1, String password2) throws ExceptionBadRq {
        if (password1 == null || !password1.equals(password2)) { //добавить аннтотации в класс для проверок на null итп
            throw new ExceptionBadRq("Пароли не совпадают");
        }
    }

    public <T> void validationCode(String code1, String code2) throws ExceptionBadRq {
        if (!code1.equals(code2)) {
            throw new ExceptionBadRq("Введенный код не совпадает с кодом картинки");
        }
    }

    public <T> void validationEmail(String email) throws ExceptionBadRq {
        if (email.isEmpty()) {
            throw new ExceptionBadRq("Поле 'email' не заполнено");
        }
    }

    public <T> void validationUser() throws ExceptionBadRq {
        throw new ExceptionBadRq("Пользователь не найден");
    }

    public <T> void validationPassword(String decodedPassword, String password) throws ExceptionBadRq {
        if (!decodedPassword.equals(password)) {
            throw new ExceptionBadRq("Не верный пароль");
        }
    }

    public void checkUserAvailability(String email) throws ExceptionBadRq {
        StringBuilder sb = new StringBuilder("Пользователь с email: '");
        sb.append(email);
        sb.append("' уже зарегистрирован");
        throw new ExceptionBadRq(sb.toString());
    }
}
