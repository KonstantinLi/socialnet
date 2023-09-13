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
  private long id;

  /** ссылка на сущность */
  @Column(name = "entity_id")
  private long entityId;

  /** тип */
  @Column(name = "type")
  @Enumerated(EnumType.ORDINAL)
  private LikeType type;

  /** Дата и время события */
  @Column(name = "time")
  private LocalDateTime time;

  /** Автор  поста */
  @ManyToOne
  @JoinColumn(name = "person_id", nullable = false, referencedColumnName = "id", foreignKey = @ForeignKey(name = "fk_like_person"))
  private Person person;

}