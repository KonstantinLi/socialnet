package ru.skillbox.socialnet.dto.request;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class UserRq {

    private String about;
    private String city;
    private String country;
    private String phone;
    private String birthDate;
    private String firstName;
    private String lastName;
    private String messagesPermission;
    private String photoId;
}
