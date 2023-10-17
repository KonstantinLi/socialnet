package ru.skillbox.socialnet.entity.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum NotificationType {
  COMMENT_COMMENT("Ответы на комментарии"),
  FRIEND_BIRTHDAY("Дни рождения у друзей"),
  FRIEND_REQUEST("Запросы в друзья"),
  MESSAGE("Сообщения"),
  POST("Публикации постов"),
  POST_COMMENT("Комментарии"),
  POST_LIKE("Лайки к посту");

  private final String description;
}
