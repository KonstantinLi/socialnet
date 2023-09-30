package ru.skillbox.socialnet.dto.response;

import lombok.Data;

@Data
public class WeatherRs {
    private String city;
    private String clouds;
    private String date;
    private String temp;
}
