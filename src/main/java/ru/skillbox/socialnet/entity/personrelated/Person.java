package ru.skillbox.socialnet.entity.personrelated;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import ru.skillbox.socialnet.entity.enums.MessagePermission;
import ru.skillbox.socialnet.entity.postrelated.Like;
import ru.skillbox.socialnet.entity.postrelated.Post;

import java.time.LocalDateTime;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "persons")
public class Person {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * имя
     */
    @Column(name = "first_name")
    private String firstName;

    /**
     * фамилия
     */
    @Column(name = "last_name")
    private String lastName;

    /**
     * дата и время регистрации
     */
    @Column(name = "reg_date")
    private LocalDateTime regDate;

    /**
     * дата рождения
     */
    @Column(name = "birth_date")
    private LocalDateTime birthDate;

    /**
     * email
     */
    @Column(name = "e_mail")
    private String email;

    /**
     * phone
     */
    @Column(name = "phone")
    private String phone;

    /**
     * пароль
     */
    @Column(name = "password")
    private String password;

    /**
     * ссылка на изображение
     */
    @Column(name = "photo")
    private String photo;

    /**
     * текст о себе
     */
    @Column(name = "about")
    private String about;

    /**
     * город проживания
     */
    @Column(name = "city")
    private String city;

    /**
     * страна проживания
     */
    @Column(name = "country")
    private String country;

    /**
     * Токен для изменения пароля
     */
    @Column(name = "change_password_token")
    private String changePasswordToken;

    /**
     * Конфигурационный код
     */
    @Column(name = "configuration_code")
    private Integer configurationCode;

    /**
     * Дата и время удаления
     */
    @Column(name = "deleted_time")
    private LocalDateTime deletedTime;

    /**
     * время последнего пребывания онлайн
     */
    @Column(name = "last_online_time")
    private LocalDateTime lastOnlineTime;

    /**
     * подтверждена ли регистрация
     */
    @Column(name = "is_approved")
    private Boolean isApproved;

    /**
     * блокировка пользователя модератором / администратором
     */
    @Column(name = "is_blocked")
    private Boolean isBlocked;

    /**
     * Удален
     */
    @Column(name = "is_deleted")
    private Boolean isDeleted;

    /**
     * Статус
     */
    @Column(name = "online_status")
    private Boolean onlineStatus;

    /**
     * идентификатор сессии уведомлений
     */
    @Column(name = "notifications_session_id")
    private String notificationSessionId;

    /**
     * разрешение на получение сообщений: ALL - от всех пользователей (кроме заблокированных), FRIENDS - только от друзей
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "message_permissions")
    private MessagePermission messagePermissions;

    @OneToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "person_settings_id", nullable = false, referencedColumnName = "id", foreignKey = @ForeignKey(name = "fk_person_settings"))
    private PersonSettings personSettings;


    /**
     * ссылка на телеграмм ?
     */
    @Column(name = "telegram_id")
    private Long telegramId;


    @OneToMany(cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.LAZY
    )
    @JoinColumn(name = "person_id", foreignKey = @ForeignKey(name = "fk_like_person"))
    private Set<Like> likes;

    @OneToMany(cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.LAZY
    )
    @JoinColumn(name = "author_id", foreignKey = @ForeignKey(name = "fk_person"))
    private Set<Post> posts;
}
