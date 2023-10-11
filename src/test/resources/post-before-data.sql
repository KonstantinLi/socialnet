drop table if exists person_settings cascade;
drop table if exists persons cascade;
drop table if exists posts cascade;

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

              create table posts (
                 id bigserial not null,
                 title varchar(255) not null,
                 post_text text not null,
                 time timestamp(6) not null,
                 is_blocked boolean not null,
                 is_deleted boolean not null,
                 time_delete timestamp(6),
                 author_id bigint not null,
                 primary key (id)
                );

insert into person_settings (comment_comment, friend_birthday, friend_request, post_like, message, post_comment, post) values (false, false, true, false, false, false, true);
insert into person_settings (comment_comment, friend_birthday, friend_request, post_like, message, post_comment, post) values (true, true, false, false, false, false, true);
insert into person_settings (comment_comment, friend_birthday, friend_request, post_like, message, post_comment, post) values (true, true, false, false, false, false, true);

insert into persons (first_name, last_name, e_mail, phone, reg_date, birth_date, city, country, deleted_time, last_online_time, is_approved, is_blocked, is_deleted, person_settings_id, password, message_permissions, online_status) values ('Ella', 'Chaplin', 'echaplin0@thetimes.co.uk', '+505-472-972-1137', '2023-07-26 22:25:20', '2006/08/28', 'Las Sabanas', 'Nicaragua', null, '2023-09-15 17:06:48', false, true, true, 1, 'MSFnNmswZjQ=', 'ALL', false);
insert into persons (first_name, last_name, e_mail, phone, reg_date, birth_date, city, country, deleted_time, last_online_time, is_approved, is_blocked, is_deleted, person_settings_id, password, message_permissions, online_status) values ('Adriaens', 'Whillock', 'awhillock1@gov.uk', '+48-832-911-0867', '2023-01-02 18:51:39', '1989/05/26', 'Dobczyce', 'Poland', null, '2023-08-01 00:59:05', false, true, false, 2, 'MSFnNmswZjQ=', 'ALL', false);

insert into posts (author_id, is_blocked, is_deleted, time, title, post_text) values (1, false, true, '2023-04-29 06:21:49', 'post 1', 'text test post 1');
insert into posts (author_id, is_blocked, is_deleted, time, title, post_text) values (2, true, false, '2023-07-25 23:47:43', 'post 2', 'text test post 2');




