package ru.skillbox.socialnet.dto.response;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class PersonRs {
    private String about;
    private String city;
    private String country;
    private CurrencyRs currency;
    private String email;
    private Long id;
    private Boolean online;
    private String phone;
    private String photo;
    private String token;
    private WeatherRs weather;
    private String birthDate;
    private String firstName;
    private String friendStatus;
    private Boolean isBlocked;
    private Boolean isBlockedByCurrentUser;
    private String lastName;
    private String lastOnlineTime;
    private String messagesPermission;
    private String regDate;
    private Boolean userDeleted;
}
