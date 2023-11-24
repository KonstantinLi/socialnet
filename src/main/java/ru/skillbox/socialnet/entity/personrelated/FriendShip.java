package ru.skillbox.socialnet.entity.personrelated;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import ru.skillbox.socialnet.entity.enums.FriendShipStatus;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "friendships")
public class FriendShip {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "sent_time")
    private LocalDateTime sentTime;

    @Column(name = "status_name")
    @Enumerated(EnumType.STRING)
    private FriendShipStatus status;


    @ManyToOne()
    @JoinColumn(name = "dst_person_id", nullable = false, referencedColumnName = "id", foreignKey = @ForeignKey(name = "fk_friendship_person_dst"))
    private Person destinationPerson;

    @ManyToOne()
    @JoinColumn(name = "src_person_id", nullable = false, referencedColumnName = "id", foreignKey = @ForeignKey(name = "fk_friendship_person_src"))
    private Person sourcePerson;
}