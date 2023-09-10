package ru.skillbox.socialnet.data.model.other;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "storage")
public class StorageEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long id;

  /** Ид владелеца */
  @Column(name = "owner_id")
  private long ownerId;

  /** Наименование файла */
  @Column(name = "file_name")
  private String fileName;

  /** Размер файла */
  @Column(name = "file_size")
  private long fileSize;

  /** Тип файла */
  @Column(name = "file_type")
  private String fileType;

  /** Дата и время */
  @Column(name = "created_at")
  private LocalDateTime createdAt;

}