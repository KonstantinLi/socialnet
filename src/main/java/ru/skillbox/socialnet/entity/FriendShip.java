package ru.skillbox.socialnet.entity;

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
import ru.skillbox.socialnet.entity.enums.FriendShipStatus;

@Getter
@Setter
@Entity
@Table(name = "friendships")
public class FriendShip {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long id;

  /** Дата и время отправки */
  @Column(name = "sent_time")
  private LocalDateTime sentTime;

  @ManyToOne
  @JoinColumn(name = "dst_person_id", nullable = false, referencedColumnName = "id", foreignKey = @ForeignKey(name = "fk_friendship_person_dst"))
  private Person destinationPerson;

  @ManyToOne
  @JoinColumn(name = "src_person_id", nullable = false, referencedColumnName = "id", foreignKey = @ForeignKey(name = "fk_friendship_person_src"))
  private Person sourcePerson;

  /** статус */
  @Column(name = "status_name")
  @Enumerated(EnumType.STRING)
  private FriendShipStatus status;

}