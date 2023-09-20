package ru.skillbox.socialnet.util;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.skillbox.socialnet.exception.CommonException;
import ru.skillbox.socialnet.exception.ErrorHandler;

@Service
@RequiredArgsConstructor
public class ValidationUtilsRq extends RuntimeException {
 private final ErrorHandler errorHandler;
    public <T> void validationRegPassword(String password1, String password2) throws CommonException {
        if (password1 == null || !password1.equals(password2)) { //добавить аннтотации в класс для проверок на null итп
            throw new CommonException("Пароли не совпадают");
        }
    }

    public <T> void validationCode(String code1, String code2) throws CommonException {
        if (!code1.equals(code2)) {
            throw new CommonException("Введенный код не совпадает с кодом картинки");
        }
    }

    public <T> void validationEmail(String email) throws CommonException {
        if (email.isEmpty()) {
            throw new CommonException("Поле 'email' не заполнено");
        }
    }

    public <T> void validationUser() throws CommonException {
            throw new CommonException("Пользователь не найден");
    }

    public <T> void validationPassword(String decodedPassword, String password) throws CommonException {
        if (decodedPassword.equals(password)) {
            throw new CommonException("Не верный пароль");
        }
    }
}
