package ru.skillbox.socialnet.data.entity.other;

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
@Table(name = "weather")
public class Weather {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long id;

  /** ID */
  @Column(name = "open_weather_id")
  private long openWeatherId;

  /** Город */
  @Column(name = "city")
  private String city;

  /** Описание погоды */
  @Column(name = "clouds")
  private String clouds;

  /** Дата */
  @Column(name = "date")
  private LocalDateTime date;

}