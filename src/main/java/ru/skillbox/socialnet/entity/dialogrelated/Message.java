package ru.skillbox.socialnet.entity.dialogrelated;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;
import ru.skillbox.socialnet.entity.enums.ReadStatus;
import ru.skillbox.socialnet.entity.personrelated.Person;

@Getter
@Setter
@Entity
@Table(name = "messages")
public class Message {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "is_deleted")
  private boolean isDeleted;

  /** Текст сообщения */
  @Column(name = "message_text", columnDefinition = "text")
  private String messageText;

  /** Статус сообщения */
  @Column(name = "read_status")
  @Enumerated(EnumType.STRING)
  private ReadStatus readStatus;

  /** Дата и время сообщения */
  @Column(name = "time")
  private LocalDateTime time;

  /** Автор  сообщения */
  @ManyToOne
  @JoinColumn(name = "author_id", nullable = false, referencedColumnName = "id", foreignKey = @ForeignKey(name = "fk_messages_author"))
  private Person author;

  /** Получатель  сообщения */
  @ManyToOne
  @JoinColumn(name = "recipient_id", nullable = false, referencedColumnName = "id", foreignKey = @ForeignKey(name = "fk_messages_recipient"))
  private Person recipient;

  /** Диалог */
  @ManyToOne
  @JoinColumn(name = "dialog_id", nullable = false, referencedColumnName = "id", foreignKey = @ForeignKey(name = "fk_messages_dialog"))
  private Dialog dialog;
}