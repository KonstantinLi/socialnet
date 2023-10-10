package ru.skillbox.socialnet.entity.postrelated;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import ru.skillbox.socialnet.entity.personrelated.Person;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "block_history")
public class BlockHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Действие
     */
    @Column(name = "comment_text")
    private String action;

    /**
     * Дата и время события
     */
    @Column(name = "time")
    private LocalDateTime time;

    /**
     * Автор  поста
     */
    @ManyToOne
    @JoinColumn(name = "author_id", nullable = false, referencedColumnName = "id", foreignKey = @ForeignKey(name = "fk_block_history_person"))
    private Person author;

    /**
     * Пост
     */
    @ManyToOne
    @JoinColumn(name = "post_id", nullable = false, referencedColumnName = "id", foreignKey = @ForeignKey(name = "fk_block_history_post"))
    private Post post;

    /**
     * Комментарий
     */
    @ManyToOne
    @JoinColumn(name = "comment_id", nullable = false, referencedColumnName = "id", foreignKey = @ForeignKey(name = "fk_block_history_comment"))
    private PostComment comment;
}