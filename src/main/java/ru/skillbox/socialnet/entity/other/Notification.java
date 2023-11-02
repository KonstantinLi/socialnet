package ru.skillbox.socialnet.entity.other;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import ru.skillbox.socialnet.entity.enums.NotificationType;
import ru.skillbox.socialnet.entity.personrelated.Person;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "notifications")
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * контакт
     */
    @Column(name = "contact")
    private String contact;

    /**
     * тип
     */
    @Column(name = "notification_type")
    @Enumerated(EnumType.STRING)
    private NotificationType notificationType;

    /**
     * ссылка на сущность
     */
    @Column(name = "entity_id")
    private long entityId;

    @Column(name = "is_read")
    private boolean isRead;

    /**
     * Дата и время отправки уведомления
     */
    @Column(name = "sent_time")
    private LocalDateTime sentTime;

    /**
     * Для кого уведомление
     */
    @ManyToOne
    @JoinColumn(name = "person_id", nullable = false, referencedColumnName = "id", foreignKey = @ForeignKey(name = "fk_notification_person"))
    private Person person;

    /**
     * От кого уведомление
     */
    @ManyToOne
    @JoinColumn(name = "sender_id", nullable = false, referencedColumnName = "id", foreignKey = @ForeignKey(name = "fk_notification_sender"))
    private Person sender;
}