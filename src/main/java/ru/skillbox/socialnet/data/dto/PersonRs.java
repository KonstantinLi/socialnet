package ru.skillbox.socialnet.data.dto;

import lombok.Data;

@Data
public class PersonRs {

    private String about;
    private String city;
    private String country;
    private CurrencyRs currency;
    private String email;
    private Integer id;
    private Boolean online;
    private String phone;
    private String photo;
    private String token;
    private WeatherRs weather;
    private String birth_date;
    private String first_name;
    private String friend_status;
    private Boolean is_blocked;
    private Boolean is_blocked_by_current_user;
    private String last_name;
    private String last_online_time;
    private String messages_permission;
    private String reg_date;
    private Boolean user_deleted;

}
