package ru.skillbox.socialnet.entity.dialogrelated;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import ru.skillbox.socialnet.entity.personrelated.Person;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "messages")
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "is_deleted")
    private boolean isDeleted;

    @Column(name = "message_text", columnDefinition = "text")
    private String messageText;

    @Column(name = "read_status")
    private String readStatus;

    @Column(name = "time")
    private LocalDateTime time;

    @ManyToOne
    @JoinColumn(name = "author_id",
            nullable = false,
            referencedColumnName = "id",
            foreignKey = @ForeignKey(name = "fk_messages_author"))
    private Person author;

    @ManyToOne
    @JoinColumn(name = "recipient_id",
            nullable = false,
            referencedColumnName = "id",
            foreignKey = @ForeignKey(name = "fk_messages_recipient"))
    private Person recipient;

    @ManyToOne
    @JoinColumn(name = "dialog_id",
            nullable = false,
            referencedColumnName = "id",
            foreignKey = @ForeignKey(name = "fk_messages_dialog"))
    private Dialog dialog;
}