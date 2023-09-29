package ru.skillbox.socialnet.exception.old;

import ru.skillbox.socialnet.exception.old.BadRequestException;

public class PersonNotFoundExeption extends BadRequestException {
    public PersonNotFoundExeption() {
        super("Запись о пользователе не найдена.");
    }
}
