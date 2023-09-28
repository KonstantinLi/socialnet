package ru.skillbox.socialnet.entity.postrelated;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import ru.skillbox.socialnet.entity.personrelated.Person;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "likes")
public class Like {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    /**
     * Ссылка на сущность
     */
    @Column(name = "entity_id")
    private long entityId;

    @Column(name = "type")
    private String type;

    /**
     * Дата и время
     */
    @Column(name = "time")
    private LocalDateTime time;

    //TODO автор поста или лайка?
    /**
     * Автор поста
     */
    @ManyToOne
    @JoinColumn(name = "person_id",
            nullable = false,
            referencedColumnName = "id",
            foreignKey = @ForeignKey(name = "fk_like_person"))
    private Person person;

}