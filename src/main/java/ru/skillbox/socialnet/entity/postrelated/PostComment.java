package ru.skillbox.socialnet.entity.postrelated;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import ru.skillbox.socialnet.entity.personrelated.Person;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "post_comments")
public class PostComment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Текст комментария
     */
    @Column(name = "comment_text", columnDefinition = "text")
    private String commentText;

    /**
     * Заблокирован
     */
    @Column(name = "is_blocked")
    private Boolean isBlocked;

    /**
     * Удален
     */
    @Column(name = "is_deleted")
    private Boolean isDeleted;

    /**
     * Дата и время создания
     */
    @Column(name = "time")
    private LocalDateTime time;

    @Column(name = "parent_id")
    private Long parentId;

    /**
     * Автор  поста
     */
    @ManyToOne
    @JoinColumn(
            name = "author_id",
            nullable = false,
            referencedColumnName = "id",
            foreignKey = @ForeignKey(name = "fk_comment_person")
    )
    private Person author;

    @OneToMany
    @JoinColumn(
            name = "parent_id",
            foreignKey = @ForeignKey(name = "fk_comment_parent_id")
    )
    private Set<PostComment> subComments = new HashSet<>();

    /**
     * Пост
     */
//    @ManyToOne
//    @JoinColumn(
//            name = "post_id",
//            nullable = false,
//            referencedColumnName = "id",
//            foreignKey = @ForeignKey(name = "fk_comment_post")
//    )
//    private Post post;

    @Column(name = "post_id")
    private Long postId;
}