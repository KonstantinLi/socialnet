package ru.skillbox.socialnet.data.model.post;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import ru.skillbox.socialnet.data.model.PersonEntity;

@Getter
@Setter
@Entity
@Table(name = "posts")
public class PostEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long id;

  /** Заблокирован */
  @Column(name = "is_blocked")
  private boolean isBlocked;

  /** Удален */
  @Column(name = "is_deleted")
  private boolean isDeleted;

  /** Дата и время создания */
  @Column(name = "time")
  private LocalDateTime time;

  /** Дата и время удаления */
  @Column(name = "time_delete")
  private LocalDateTime timeDelete;

  /** Заголовок поста */
  @Column(name = "title")
  private String title;

  /** Текст поста */
  @Column(name = "post_text", columnDefinition = "text")
  private String postText;

  /** Автор  поста */
  @ManyToOne
  @JoinColumn(name = "author_id", nullable = false, referencedColumnName = "id", foreignKey = @ForeignKey(name = "fk_person"))
  private PersonEntity author;

  /** Теги  поста */
  @ManyToMany(fetch = FetchType.LAZY)
  @JoinTable(name = "post2tag", joinColumns = @JoinColumn(name = "post_id"), inverseJoinColumns = @JoinColumn(name = "tag_id"))
  private List<TagEntity> tags = new ArrayList<>();

  /** Файлы в посте */
  @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
  private List<PostFileEntity> files;

}