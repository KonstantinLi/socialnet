package ru.skillbox.socialnet.entity.locationrelated;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Entity
@Table(name = "cities")
public class City {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "name")
    private String name;

    @ManyToOne
    @JoinColumn(name = "country_id", nullable = false, referencedColumnName = "id", foreignKey = @ForeignKey(name = "fk_country"))
    private Country country;

    @Column(name = "state")
    private String state;

    @Column(name = "lat", columnDefinition = "numeric")
    private BigDecimal lat;

    @Column(name = "lng", columnDefinition = "numeric")
    private BigDecimal lng;

    @Column(name = "open_weather_id")
    private long openWeatherId;

    /**
     * Код страны
     */
    @Column(name = "code2")
    private String code2;

    @Column(name = "international_name")
    private String internationalName;
}