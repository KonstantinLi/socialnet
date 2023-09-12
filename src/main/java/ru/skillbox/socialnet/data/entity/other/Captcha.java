package ru.skillbox.socialnet.data.entity.other;

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
@Table(name = "captcha")
public class Captcha {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long id;

  /** Код */
  @Column(name = "code")
  private String code;

  /** Секретный код */
  @Column(name = "secret_code")
  private String secretСode;

  /** Дата и время */
  @Column(name = "time")
  private String time;

}