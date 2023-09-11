package ru.skillbox.socialnet.data.entity.post;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import ru.skillbox.socialnet.data.entity.Person;

@Getter
@Setter
@Entity
@Table(name = "post_comments")
public class PostComment {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long id;

  /** Текст комментария */
  @Column(name = "comment_text", columnDefinition = "text")
  private String commentText;

  /** Заблокирован */
  @Column(name = "is_blocked")
  private boolean isBlocked;

  /** Удален */
  @Column(name = "is_deleted")
  private boolean isDeleted;

  /** Дата и время создания */
  @Column(name = "time")
  private LocalDateTime time;

  /** Автор  поста */
  @ManyToOne
  @JoinColumn(name = "author_id", nullable = false, referencedColumnName = "id", foreignKey = @ForeignKey(name = "fk_comment_person"))
  private Person author;

  @OneToMany
  @JoinColumn(name = "parent_id", foreignKey = @ForeignKey(name = "fk_comment_parent_id"))
  private List<PostComment> children = new ArrayList<>();

  /** Пост */
  @ManyToOne
  @JoinColumn(name = "post_id", nullable = false, referencedColumnName = "id", foreignKey = @ForeignKey(name = "fk_comment_post"))
  private Post post;
}