package ru.skillbox.socialnet.data.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "person_settings")
public class PersonSettingsEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long id;

  @Column(name = "comment_comment")
  private boolean commentComment;

  @Column(name = "friend_birthday")
  private boolean friendBirthday;

  @Column(name = "friend_request")
  private boolean friend_request;

  @Column(name = "post_like")
  private boolean postLike;

  @Column(name = "message")
  private boolean message;

  @Column(name = "post_comment")
  private boolean postComment;

  @Column(name = "post")
  private boolean post;

}