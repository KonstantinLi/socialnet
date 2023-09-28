package ru.skillbox.socialnet.entity.locationrelated;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "weather")
public class Weather {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "open_weather_id")
    private Long openWeatherId;

    @Column(name = "city")
    private String city;

    /**
     * Описание погоды
     */
    @Column(name = "clouds")
    private String clouds;

    @Column(name = "date")
    private LocalDateTime date;

}