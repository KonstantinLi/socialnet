package ru.skillbox.socialnet.entity.post;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;
import ru.skillbox.socialnet.entity.Person;

@Getter
@Setter
@Entity
@Table(name = "posts")
public class Post {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  /** Заблокирован */
  @Column(name = "is_blocked")
  private Boolean isBlocked;

  /** Удален */
  @Column(name = "is_deleted")
  private Boolean isDeleted;

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

  /** Автор  поста
  @Column(name = "author_id", nullable = false)
  private Long authorId; */
  /** Автор  поста */
  @ManyToOne
  @JoinColumn(name = "author_id", nullable = false, referencedColumnName = "id", foreignKey = @ForeignKey(name = "fk_person"))
  private Person author;



  /** Теги  поста */
  @ManyToMany
  @JoinTable(name = "post2tag", joinColumns = @JoinColumn(name = "post_id"), inverseJoinColumns = @JoinColumn(name = "tag_id"))
  private List<Tag> tags;

  @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<PostComment> comments;

  /** Файлы в посте */
  @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<PostFile> files;
}