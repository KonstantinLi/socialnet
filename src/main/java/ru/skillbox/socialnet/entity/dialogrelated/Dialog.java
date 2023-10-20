package ru.skillbox.socialnet.entity.dialogrelated;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import ru.skillbox.socialnet.entity.personrelated.Person;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "dialogs")
public class Dialog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Первый участник
     */
    @ManyToOne
    @JoinColumn(name = "first_person_id",
            nullable = false,
            referencedColumnName = "id",
            foreignKey = @ForeignKey(name = "fk_dialog_first_person"))
    private Person firstPerson;

    /**
     * Второй участник
     */
    @ManyToOne
    @JoinColumn(name = "second_person_id",
            nullable = false,
            referencedColumnName = "id",
            foreignKey = @ForeignKey(name = "fk_dialog_second_person"))
    private Person secondPerson;

    /**
     * Дата и время последнего общения
     */
    @Column(name = "last_active_time")
    private LocalDateTime lastActiveTime;

    /**
     * Ссылка на последнее сообщение
     */
    @Column(name = "last_message_id")
    private Long lastMessageId;
}