package ru.skillbox.socialnet.entity.post;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
import ru.skillbox.socialnet.entity.Person;

@Getter
@Setter
@Entity
@Table(name = "block_history")
public class BlockHistory {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long id;

  /** Действие */
  @Column(name = "comment_text")
  private String action;

  /** Дата и время события */
  @Column(name = "time")
  private LocalDateTime time;

  /** Автор  поста */
  @ManyToOne
  @JoinColumn(name = "author_id", nullable = false, referencedColumnName = "id", foreignKey = @ForeignKey(name = "fk_block_history_person"))
  private Person author;

  /** Пост */
  @ManyToOne
  @JoinColumn(name = "post_id", nullable = false, referencedColumnName = "id", foreignKey = @ForeignKey(name = "fk_block_history_post"))
  private Post post;

  /** Комментарий */
  @ManyToOne
  @JoinColumn(name = "comment_id", nullable = false, referencedColumnName = "id", foreignKey = @ForeignKey(name = "fk_block_history_comment"))
  private PostComment comment;
}