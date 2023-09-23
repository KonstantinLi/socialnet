package ru.skillbox.socialnet.exception;

import ru.skillbox.socialnet.errs.BadRequestException;

public class PersonNotFoundExeption extends BadRequestException{
    public PersonNotFoundExeption() {
        super("Запись о пользователе не найдена.");
    }
}
