package com.socialnet.entity.locationrelated;

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
    private Long id;

    /**
     * ID
     */
    @Column(name = "open_weather_ids")
    private String openWeatherIds;

    /**
     * Город
     */
    @Column(name = "city")
    private String city;

    /**
     * Описание погоды
     */
    @Column(name = "clouds")
    private String clouds;

    /**
     * Дата
     */
    @Column(name = "date")
    private LocalDateTime date;

    /**
     * Температура
     */
    @Column(name = "temp")
    private Double temp;

}