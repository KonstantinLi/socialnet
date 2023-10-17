package ru.skillbox.socialnet.exception;

public class MessageException extends CommonException{

  public MessageException(String message) {
    super(message);
  }

  public static MessageException messageNotFound(Long messageId){
    return new MessageException(String.format("Не найдено сообщение с кодом %s", messageId));
  }

  public static MessageException dialogNotFound(Long dialogId){
    return new MessageException(String.format("Не найден диалог с кодом %s", dialogId));
  }


}
