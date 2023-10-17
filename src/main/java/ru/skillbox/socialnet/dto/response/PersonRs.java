package ru.skillbox.socialnet.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import ru.skillbox.socialnet.entity.enums.MessagePermission;

@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PersonRs {

    private String about;
    @Schema(example = "Moscow")
    private String city;
    @Schema(example = "Russia")
    private String country;
    private CurrencyRs currency;
    @Schema(example = "mail@@email.ru")
    private String email;
    @Schema(example = "1")
    private Long id;
    @Schema(example = "true")
    private Boolean online;
    @Schema(example = "+79250101122")
    private String phone;
    private String photo;
    @Schema(example = "JWT token")
    private String token;
    private WeatherRs weather;
    @Schema(example = "01.01.2000")
    private String birthDate;
    @Schema(example = "Иван")
    private String firstName;
    private String friendStatus;
    @Schema(example = "false")
    private Boolean isBlocked;
    @Schema(example = "false")
    private Boolean isBlockedByCurrentUser;
    @Schema(example = "Иванов")
    private String lastName;
    @Schema(example = "01.01.2023 01:00")
    private String lastOnlineTime;
    private MessagePermission messagesPermission;
    @Schema(example = "01.01.2000")
    private String regDate;
    @Schema(example = "false")
    private Boolean userDeleted;
}
