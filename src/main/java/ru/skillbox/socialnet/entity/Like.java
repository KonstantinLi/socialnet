package ru.skillbox.socialnet.entity;

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

@Getter
@Setter
@Entity
@Table(name = "likes")
public class Like {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long id;

  /** ссылка на сущность */
  @Column(name = "entity_id")
  private long entityId;

  /** тип */
  @Column(name = "type")
  private String type;

  /** Дата и время события */
  @Column(name = "time")
  private LocalDateTime time;

  /** Автор  поста */
  @ManyToOne
  @JoinColumn(name = "person_id", nullable = false, referencedColumnName = "id", foreignKey = @ForeignKey(name = "fk_like_person"))
  private Person person;

}