package ru.skillbox.socialnet.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;
import ru.skillbox.socialnet.entity.enums.LikeType;

@Getter
@Setter
@Entity
@Table(name = "likes")
public class Like {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  /** ссылка на сущность */
  @Column(name = "entity_id")
  private Long entityId;

  /** тип */
  @Column(name = "type")
  @Enumerated(EnumType.ORDINAL)
  private LikeType type;

  /** Дата и время события */
  @Column(name = "time")
  private LocalDateTime time = LocalDateTime.now();

  /** Автор  поста */
  @Column(name = "person_id", nullable = false)
  private Long personId;
}