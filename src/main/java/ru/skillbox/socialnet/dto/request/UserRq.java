package ru.skillbox.socialnet.dto.request;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
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
