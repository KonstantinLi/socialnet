drop table if exists person_settings cascade;
drop table if exists persons cascade;
drop table if exists dialogs cascade;
drop table if exists messages cascade;
drop table if exists countries cascade;


    create table person_settings (
        id bigserial not null,
        comment_comment boolean,
        friend_birthday boolean,
        friend_request boolean,
        message boolean,
        post boolean,
        post_comment boolean,
        post_like boolean,
        primary key (id)
    );

    create table persons (
        id bigserial not null,
        first_name varchar(255),
        last_name varchar(255),
        e_mail varchar(255),
        phone varchar(255),
        birth_date timestamp(6),
        about varchar(255),
        change_password_token varchar(255),
        city varchar(255),
        configuration_code integer,
        country varchar(255),
        is_approved boolean,
        is_blocked boolean,
        is_deleted boolean,
        deleted_time timestamp(6),
        last_online_time timestamp(6),
        message_permissions varchar(255) not null,
        notifications_session_id varchar(255),
        online_status boolean not null,
        password varchar(255),
        photo varchar(255),
        reg_date timestamp(6),
        telegram_id bigint,
        person_settings_id bigint not null,
        unique (e_mail),
        primary key (id)
    );

    create table dialogs (
        id bigserial not null,
        last_active_time timestamp(6),
        last_message_id bigint,
        first_person_id bigint not null,
        second_person_id bigint not null,
        primary key (id)
    );

    create table messages (
          id bigserial not null,
          is_deleted boolean,
          message_text text,
          read_status varchar(255),
          time timestamp(6),
          author_id bigint not null,
          dialog_id bigint not null,
          recipient_id bigint not null,
          primary key (id)
    );

    CREATE TABLE countries (
      "id" SERIAL,
      "code2" VARCHAR(255) NULL,
      "full_name" VARCHAR(255) NULL,
      "international_name" VARCHAR(255) NULL,
      "name" VARCHAR(255) NULL,
      "external_id" BIGINT NULL,
      CONSTRAINT "countries_pkey" PRIMARY KEY ("id")
    );


    insert into person_settings (comment_comment, friend_birthday, friend_request, post_like, message, post_comment, post) values (false, false, true, false, false, false, true);
    insert into person_settings (comment_comment, friend_birthday, friend_request, post_like, message, post_comment, post) values (true, true, false, false, false, false, true);
    insert into person_settings (comment_comment, friend_birthday, friend_request, post_like, message, post_comment, post) values (true, false, false, false, true, true, false);

    insert into persons (first_name, last_name, e_mail, phone, reg_date, birth_date, city, country, deleted_time, last_online_time, is_approved, is_blocked, is_deleted, person_settings_id, password, message_permissions, online_status) values ('Ella', 'Chaplin', 'echaplin0@thetimes.co.uk', '+505-472-972-1137', '2023-07-26 22:25:20', '2006/08/28', 'Las Sabanas', 'Nicaragua', null, '2023-09-15 17:06:48', true, false, false, 1, 'MSFnNmswZjQ=', 'ALL', false);
    insert into persons (first_name, last_name, e_mail, phone, reg_date, birth_date, city, country, deleted_time, last_online_time, is_approved, is_blocked, is_deleted, person_settings_id, password, message_permissions, online_status) values ('Adriaens', 'Whillock', 'awhillock1@gov.uk', '+48-832-911-0867', '2023-01-02 18:51:39', '1989/05/26', 'Dobczyce', 'Poland', null, '2023-08-01 00:59:05', true, false, false, 2, 'MSFnNmswZjQ=', 'ALL', false);
    insert into persons (first_name, last_name, e_mail, phone, reg_date, birth_date, city, country, deleted_time, last_online_time, is_approved, is_blocked, is_deleted, person_settings_id, password, message_permissions, online_status) values ('Ailsun', 'Asbury', 'aasbury2@paginegialle.it', '+46-328-795-3579', '2023-06-13 19:02:09', '2008/02/24', 'Stockholm', 'Sweden', null, null, true, false, false, 3, 'MSFnNmswZjQ=', 'ALL', false);

    INSERT INTO dialogs (last_active_time, last_message_id, first_person_id, second_person_id) VALUES('2023-11-02 05:11:38.281', 4, 1, 2);
    INSERT INTO dialogs (last_active_time, last_message_id, first_person_id, second_person_id) VALUES('2023-10-31 15:38:51.065', 7, 3, 2);

    INSERT INTO messages (is_deleted, message_text, read_status, "time", author_id, dialog_id, recipient_id) VALUES(false, 'Создан диалог', 'READ', '2023-10-30 11:47:40.786', 1, 1, 2);
    INSERT INTO messages (is_deleted, message_text, read_status, "time", author_id, dialog_id, recipient_id) VALUES(false, 'Создан диалог', 'READ', '2023-10-31 15:24:01.333', 3, 2, 2);
    INSERT INTO messages (is_deleted, message_text, read_status, "time", author_id, dialog_id, recipient_id) VALUES(false, 'Привет, как дела?', 'READ', '2023-10-31 15:38:51.065', 1, 1, 2);
    INSERT INTO messages (is_deleted, message_text, read_status, "time", author_id, dialog_id, recipient_id) VALUES(false, 'Привет, все хорошо', 'UNREAD', '2023-10-31 15:27:01.067', 2, 1, 1);
    INSERT INTO messages (is_deleted, message_text, read_status, "time", author_id, dialog_id, recipient_id) VALUES(false, 'Привет', 'READ', '2023-11-02 05:11:38.281', 3, 2, 2);
    INSERT INTO messages (is_deleted, message_text, read_status, "time", author_id, dialog_id, recipient_id) VALUES(false, 'Привет, какая у вас погода?', 'READ', '2023-11-02 10:58:38.410', 2, 2, 3);
    INSERT INTO messages (is_deleted, message_text, read_status, "time", author_id, dialog_id, recipient_id) VALUES(false, 'У нас снег', 'UNREAD', '2023-11-02 15:04:43.713', 3, 2, 2);

    INSERT INTO countries ("id", "code2", "full_name", "international_name", "name", "external_id") VALUES ('148', 'RU', 'Россия', 'Russia', 'Россия', '1');

