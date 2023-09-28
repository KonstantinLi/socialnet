package ru.skillbox.socialnet.entity.locationrelated;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import lombok.Getter;
import lombok.Setter;
import ru.skillbox.socialnet.entity.locationrelated.Country;

@Getter
@Setter
@Entity
@Table(name = "cities")
public class City {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long id;

  /** Наименование */
  @Column(name = "name")
  private String name;

  /** Страна */
  @ManyToOne
  @JoinColumn(name = "country_id", nullable = false, referencedColumnName = "id", foreignKey = @ForeignKey(name = "fk_country"))
  private Country country;

  @Column(name = "state")
  private String state;

  /** Широта */
  @Column(name = "lat", columnDefinition = "numeric")
  private BigDecimal lat;

  /** Долгота */
  @Column(name = "lng", columnDefinition = "numeric")
  private BigDecimal lng;

  /** ID */
  @Column(name = "open_weather_id")
  private long openWeatherId;

  /** Код страны */
  @Column(name = "code2")
  private String code2;

  /** Международное наименование */
  @Column(name = "international_name")
  private String internationalName;
}