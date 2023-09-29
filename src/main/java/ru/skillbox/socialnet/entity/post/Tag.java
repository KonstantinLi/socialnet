package ru.skillbox.socialnet.entity.post;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "tags")
public class Tag {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long id;

  /** Название тега */
  @Column(name = "tag")
  private String tag;

    @ManyToMany
    @JoinTable(name = "post2tag", joinColumns = @JoinColumn(name = "tag_id"), inverseJoinColumns = @JoinColumn(name = "post_id"))
    @JsonIgnore
    private List<Post> posts = new ArrayList<>();

  @Override
  public String toString() {
    return tag;
  }
}