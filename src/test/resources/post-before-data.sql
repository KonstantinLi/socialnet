


              drop table if exists person_settings cascade;
              drop table if exists persons cascade;
              drop table if exists post2tag cascade;
              drop table if exists post_comments cascade;
              drop table if exists post_files cascade;
              drop table if exists posts cascade;
              drop table if exists storage cascade;
              drop table if exists tags cascade;


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

              create table post2tag (
                 id bigserial not null,
                 post_id bigint not null,
                 tag_id bigint not null,
                 primary key (id)
                );

              create table post_comments (
                 id bigserial not null,
                 comment_text text not null,
                 is_blocked boolean not null,
                 is_deleted boolean not null,
                 time timestamp(6) not null,
                 author_id bigint not null,
                 post_id bigint not null,
                 parent_id bigint,
                 primary key (id)
                );

              create table post_files (
                 id bigserial not null,
                 name varchar(255),
                 path varchar(255),
                 post_id bigint not null,
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

              create table storage (
                 id bigserial not null,
                 created_at timestamp(6),
                 file_name varchar(255),
                 file_size bigint,
                 file_type varchar(255),
                 owner_id bigint,
                 primary key (id)
                );

              create table tags (
                 id bigserial not null,
                 tag varchar(255) not null,
                 unique (tag),
                 primary key (id)
                );


insert into person_settings (comment_comment, friend_birthday, friend_request, post_like, message, post_comment, post) values (false, false, true, false, false, false, true);
insert into person_settings (comment_comment, friend_birthday, friend_request, post_like, message, post_comment, post) values (true, true, false, false, false, false, true);

insert into persons (first_name, last_name, e_mail, phone, reg_date, birth_date, city, country, deleted_time, last_online_time, is_approved, is_blocked, is_deleted, person_settings_id, password, message_permissions, online_status) values ('Ella', 'Chaplin', 'echaplin0@thetimes.co.uk', '+505-472-972-1137', '2023-07-26 22:25:20', '2006/08/28', 'Las Sabanas', 'Nicaragua', null, '2023-09-15 17:06:48', false, true, true, 1, 'MSFnNmswZjQ=', 'ALL', false);
insert into persons (first_name, last_name, e_mail, phone, reg_date, birth_date, city, country, deleted_time, last_online_time, is_approved, is_blocked, is_deleted, person_settings_id, password, message_permissions, online_status) values ('Adriaens', 'Whillock', 'awhillock1@gov.uk', '+48-832-911-0867', '2023-01-02 18:51:39', '1989/05/26', 'Dobczyce', 'Poland', null, '2023-08-01 00:59:05', false, true, false, 2, 'MSFnNmswZjQ=', 'ALL', false);

insert into tags (tag) values ('morbi vel');
insert into tags (tag) values ('posuere nonummy integer');

insert into posts (author_id, is_blocked, is_deleted, time, title, post_text) values (1, false, true, '2023-04-29 06:21:49', '⁰⁴⁵', 'text test post 1');
insert into posts (author_id, is_blocked, is_deleted, time, title, post_text) values (2, true, false, '2023-07-25 23:47:43', '1', 'bibendum felis sed interdum venenatis turpis enim blandit mi in porttitor pede justo eu massa donec dapibus duis at velit eu est congue elementum in hac habitasse platea dictumst morbi vestibulum velit id pretium');

insert into post2tag (post_id, tag_id) values (1, 1);
insert into post2tag (post_id, tag_id) values (2, 2);

insert into post_comments (comment_text, is_blocked, is_deleted, time, author_id, post_id) values ('in eleifend quam a odio in hac habitasse platea dictumst maecenas ut massa', true, true, '2023-03-14 22:30:18', 1, 1);
insert into post_comments (comment_text, is_blocked, is_deleted, time, author_id, post_id) values ('nec sem duis aliquam convallis nunc proin at turpis a pede posuere', true, false, '2023-04-19 08:42:27', 1, 1);

--insert into friendships (sent_time, status_name, src_person_id, dst_person_id) values
--('2023-09-22 22:01:13', 'FRIEND', 1, 2),




