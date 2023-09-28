package ru.skillbox.socialnet.dto.response;

import lombok.Data;

@Data
public class CaptchaRs {

    private String code;
    private String image;

    @Data
    public static class CurrencyRs {

        private String euro;
        private String usd;

    }

    //TODO что тут происходит? Оо
    @Data
    public static class WeatherRs {

        private String city;
        private String clouds;
        private String date;
        private String temp;

    }
}
