package ru.skillbox.socialnet.exception;

public class PersonNotFoundExeption extends BadRequestException{
    public PersonNotFoundExeption() {
        super("Запись о пользователе не найдена.");
    }
}
