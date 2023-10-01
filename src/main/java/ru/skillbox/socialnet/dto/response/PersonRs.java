package ru.skillbox.socialnet.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
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
    @JsonProperty("birth_date")
    private String birthDate;
    @JsonProperty("first_name")
    private String firstName;
    @JsonProperty("friend_status")
    private String friendStatus;
    @JsonProperty("is_blocked")
    private Boolean isBlocked;
    @JsonProperty("is_blocked_by_current_user")
    private Boolean isBlockedByCurrentUser;
    @JsonProperty("last_name")
    private String lastName;
    @JsonProperty("last_online_time")
    private String lastOnlineTime;
    @JsonProperty("messages_permission")
    private String messagesPermission;
    @JsonProperty("reg_date")
    private String regDate;
    @JsonProperty("user_deleted")
    private Boolean userDeleted;

}
