package ru.skillbox.socialnet.entity.postrelated;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "post_files")
public class PostFile {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long id;

  /** Название */
  @Column(name = "name")
  private String name;

  /** Путь к файлу */
  @Column(name = "path")
  private String path;

  @ManyToOne
  @JoinColumn(name = "post_id", nullable = false, referencedColumnName = "id", foreignKey = @ForeignKey(name = "fk_file_post"))
  private Post post;
}