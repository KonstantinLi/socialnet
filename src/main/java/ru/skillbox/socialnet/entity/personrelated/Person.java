package ru.skillbox.socialnet.entity.personrelated;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import ru.skillbox.socialnet.entity.enums.MessagePermission;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "persons")
public class Person {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "reg_date")
    private LocalDateTime regDate;

    @Column(name = "birth_date")
    private LocalDateTime birthDate;

    @Column(name = "e_mail")
    private String email;

    @Column(name = "phone")
    private String phone;

    @Column(name = "password")
    private String password;

    @Column(name = "photo")
    private String photo;

    @Column(name = "about")
    private String about;

    @Column(name = "city")
    private String city;

    @Column(name = "country")
    private String country;

    @Column(name = "change_password_token")
    private String changePasswordToken;

    @Column(name = "configuration_code")
    private Integer configurationCode;

    @Column(name = "deleted_time")
    private LocalDateTime deletedTime;

    @Column(name = "last_online_time")
    private LocalDateTime lastOnlineTime;

    @Column(name = "is_approved")
    private Boolean isApproved;

    @Column(name = "is_blocked")
    private Boolean isBlocked;

    @Column(name = "is_deleted")
    private Boolean isDeleted;

    @Column(name = "online_status")
    private String onlineStatus;

    @Column(name = "notifications_session_id")
    private String notificationSessionId;

    @Enumerated(EnumType.STRING)
    @Column(name = "message_permissions")
    private MessagePermission messagePermission;

    @OneToOne
    @JoinColumn(name = "person_settings_id",
            nullable = false,
            referencedColumnName = "id",
            foreignKey = @ForeignKey(name = "fk_person_settings"))
    private PersonSettings personSettings;


    @Column(name = "telegram_id")
    private Long telegramId;
}
