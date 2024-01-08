package com.socialnet.entity.postrelated;

import com.socialnet.entity.enums.LikeType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "likes")
public class Like {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * ссылка на сущность
     */
    @Column(name = "entity_id")
    private Long entityId;

    /**
     * тип
     */
    @Column(name = "type")
    @Enumerated(EnumType.STRING)
    private LikeType type;

    /**
     * Дата и время события
     */
    @Column(name = "time")
    private LocalDateTime time = LocalDateTime.now();

    /**
     * Автор  поста
     */
    @Column(name = "person_id", nullable = false)
    private Long personId;
}