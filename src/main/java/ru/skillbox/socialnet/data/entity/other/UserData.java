package ru.skillbox.socialnet.data.entity.other;

import lombok.Data;

@Data
public class UserData{

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
