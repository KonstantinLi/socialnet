package ru.skillbox.socialnet.data.dto;

import lombok.Data;
import ru.skillbox.socialnet.data.entity.Person;

@Data
public class PersonRs {

    public PersonRs() {
    }

    public PersonRs(Person person) {

        this.about = person.getAbout();
        this.city = person.getCity();
        this.country = person.getCountry();

        //TODO remove plug for currency
        this.currency = new CurrencyRs();
        this.email = person.getEmail();
        this.id = person.getId();
        this.online = person.getOnlineStatus() != null && person.getOnlineStatus()
                .equalsIgnoreCase("ONLINE");
        this.phone = person.getPhone();
        this.photo = person.getPhoto();

        //TODO remove plug for weather
        this.weather = new WeatherRs();
        this.birth_date = person.getBirthDate().toString();
        this.first_name = person.getFirstName();
        this.is_blocked = person.isBlocked();
        this.is_blocked_by_current_user = false;
        this.last_name = person.getLastName();
        this.last_online_time = person.getLastOnlineTime() != null ?
                person.getLastOnlineTime().toString() : null;
        this.messages_permission = person.getMessagePermission() != null ?
                person.getMessagePermission().toString() : null;
        this.reg_date = person.getRegDate() != null ?
                person.getRegDate().toString() : null;
        this.user_deleted = person.isDeleted();
    }

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
