package ru.skillbox.socialnet.data.model.message;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
import ru.skillbox.socialnet.data.model.PersonEntity;

@Getter
@Setter
@Entity
@Table(name = "dialogs")
public class DialogEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long id;

  /** Первый участник */
  @ManyToOne
  @JoinColumn(name = "first_person_id", nullable = false, referencedColumnName = "id", foreignKey = @ForeignKey(name = "fk_dialog_first_person"))
  private PersonEntity firstPerson;

  /** Второй участник */
  @ManyToOne
  @JoinColumn(name = "second_person_id", nullable = false, referencedColumnName = "id", foreignKey = @ForeignKey(name = "fk_dialog_second_person"))
  private PersonEntity second_person_id;

  /** Дата и время последнего общения */
  @Column(name = "last_active_time")
  private LocalDateTime lastActiveTime;

  /** Ссылка на последнее сообщение */
  @Column(name = "last_message_id")
  private long lastMessageId;
}