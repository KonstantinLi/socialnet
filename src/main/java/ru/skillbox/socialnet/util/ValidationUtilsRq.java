package ru.skillbox.socialnet.util;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.skillbox.socialnet.exception.auth.ValidationException;

@Service
@RequiredArgsConstructor
public class ValidationUtilsRq extends RuntimeException {

    public <T> void validationRegPassword(String password1, String password2) {
        if (password1 == null || !password1.equals(password2)) {
            // TODO: добавить аннтотации в класс для проверок на null итп
            throw new ValidationException("Пароли не совпадают");
        }
    }

    public <T> void validationCode(String code1, String code2) {
        if (!code1.equals(code2)) {
            throw new ValidationException("Введенный код не совпадает с кодом картинки");
        }
    }

    public <T> void validationEmail(String email) {
        if (email.isEmpty()) {
            throw new ValidationException("Поле 'email' не заполнено");
        }
    }

    public <T> void validationUser() {
        throw new ValidationException("Пользователь не найден");
    }

    public <T> void validationPassword(String decodedPassword, String password) {
        if (!decodedPassword.equals(password)) {
            throw new ValidationException("Не верный пароль");
        }
    }

    public void checkUserAvailability(String email) {
        StringBuilder sb = new StringBuilder("Пользователь с email: '");
        sb.append(email);
        sb.append("' уже зарегистрирован");
        throw new ValidationException(sb.toString());
    }
}
