package com.socialnet.dto.response.openweathermap;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public final class OpenWeatherMapRs {
    private Coord coord;
    private Weather[] weather;
    private String base;
    private Main main;
    private int visibility;
    private Wind wind;
    private Clouds clouds;
    private int dt;
    private Sys sys;
    private int timezone;
    private int id;
    private String name;
    private int cod;

    @Getter
    @Setter
    public static final class Coord {
        private double lon;
        private double lat;
    }

    @Getter
    @Setter
    public static final class Weather {
        private int id;
        private String main;
        private String description;
        private String icon;
    }

    @Getter
    @Setter
    public static final class Main {
        private double temp;
        @JsonProperty("feels_like")
        private double feelsLike;
        @JsonProperty("temp_min")
        private double tempMin;
        @JsonProperty("temp_max")
        private double tempMax;
        private int pressure;
        private int humidity;
    }

    @Getter
    @Setter
    public static final class Wind {
        private int speed;
        private int deg;
    }

    @Getter
    @Setter
    private static final class Clouds {
        private int all;
    }

    @Getter
    @Setter
    public static final class Sys {
        private int type;
        private int id;
        private String country;
        private int sunrise;
        private int sunset;
    }
}
