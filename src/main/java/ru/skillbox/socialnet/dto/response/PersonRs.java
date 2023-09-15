package ru.skillbox.socialnet.dto.response;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;
import ru.skillbox.socialnet.entity.enums.MessagePermission;

@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class PersonRs {
    private String about;
    private String birthDate;
    private String city;
    private String country;
    private CurrencyRs currency;
    private String email;
    private String firstName;
    private String friendStatus;
    private Long id;
    private Boolean isBlocked;
    private Boolean isBlockedByCurrentUser;
    private String lastName;
    private String lastOnlineTime;
    private MessagePermission messagesPermission;
    private Boolean online;
    private String phone;
    private String photo;
    private String regDate;
    private String token;
    private Boolean userDeleted;
    private WeatherRs weather;
}
