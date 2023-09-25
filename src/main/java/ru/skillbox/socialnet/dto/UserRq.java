package ru.skillbox.socialnet.dto;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class UserRq {

    private String about;
    private String city;
    private String country;
    private String phone;
    private String birth_date;
    private String first_name;
    private String last_name;
    private String messages_permission;
    private String photo_id;
}
