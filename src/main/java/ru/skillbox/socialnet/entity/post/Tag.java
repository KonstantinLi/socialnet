package ru.skillbox.socialnet.entity.post;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "tags")
public class Tag {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  /** Название тега */
  @Column(name = "tag")
  private String tag;


  @ManyToMany(mappedBy = "tags")
  private Set<Post> posts = new HashSet<>();
}