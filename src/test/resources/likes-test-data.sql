drop table if exists person_settings cascade;
drop table if exists persons cascade;
drop table if exists posts cascade;
drop table if exists likes cascade;
drop table if exists post_comments cascade;


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

            create table likes (
             id bigserial not null,
             entity_id bigint not null,
             time timestamp(6) not null,
             type varchar(255) not null,
             person_id bigint not null,
             unique (person_id, type, entity_id),
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

        insert into person_settings (comment_comment, friend_birthday, friend_request, post_like, message, post_comment, post) values (false, false, true, false, false, false, true);
        insert into person_settings (comment_comment, friend_birthday, friend_request, post_like, message, post_comment, post) values (true, true, false, false, false, false, true);
        insert into person_settings (comment_comment, friend_birthday, friend_request, post_like, message, post_comment, post) values (true, false, false, false, true, true, false);
        insert into person_settings (comment_comment, friend_birthday, friend_request, post_like, message, post_comment, post) values (false, false, true, false, false, false, true);


        insert into persons (first_name, last_name, e_mail, phone, reg_date, birth_date, city, country, deleted_time, last_online_time, is_approved, is_blocked, is_deleted, person_settings_id, password, message_permissions, online_status)
            values ('Ella', 'Chaplin', 'echaplin0@thetimes.co.uk', '+505-472-972-1137', '2023-07-26 22:25:20', '2006/08/28', 'Las Sabanas', 'Nicaragua', null, '2023-09-15 17:06:48', true, false, false, 1, 'MSFnNmswZjQ=', 'ALL', false);
        insert into persons (first_name, last_name, e_mail, phone, reg_date, birth_date, city, country, deleted_time, last_online_time, is_approved, is_blocked, is_deleted, person_settings_id, password, message_permissions, online_status)
            values ('Adriaens', 'Whillock', 'awhillock1@gov.uk', '+48-832-911-0867', '2023-01-02 18:51:39', '1989/05/26', 'Dobczyce', 'Poland', null, '2023-08-01 00:59:05', true, false, false, 2, 'MSFnNmswZjQ=', 'ALL', false);
        insert into persons (first_name, last_name, e_mail, phone, reg_date, birth_date, city, country, deleted_time, last_online_time, is_approved, is_blocked, is_deleted, person_settings_id, password, message_permissions, online_status)
            values ('Ailsun', 'Asbury', 'aasbury2@paginegialle.it', '+46-328-795-3579', '2023-06-13 19:02:09', '2008/02/24', 'Stockholm', 'Sweden', null, null, true, false, false, 3, 'MSFnNmswZjQ=', 'ALL', false);

        --Blocked and deleted user
        insert into persons (first_name, last_name, e_mail, phone, reg_date, birth_date, city, country, deleted_time, last_online_time, is_approved, is_blocked, is_deleted, person_settings_id, password, message_permissions, online_status)
            values ('Kerr', 'Morena', 'kmorena3@nationalgeographic.com', '+381-453-175-4645', '2023-05-26 09:22:42', '2015/02/15', 'Trstenik', 'Serbia', null, '2023-09-25 05:04:56', false, true, true, 4, 'MSFnNmswZjQ=', 'ALL', false);


        insert into posts (id, author_id, is_blocked, is_deleted, time, title, post_text)
            values (10, 1, false, false, '2023-04-29 06:21:49', 'Камю А', 'Разум бессилен перед криком сердца');
        insert into posts (id, author_id, is_blocked, is_deleted, time, title, post_text)
            values (11, 2, true, false, '2023-07-25 23:47:43', 'Гессе Г', 'В основном свободу человек проявляет только в выборе зависимости');
        insert into posts (id, author_id, is_blocked, is_deleted, time, title, post_text)
            values (12, 1, false, true, '2023-05-09 22:58:52', 'Хемингуэй Э', 'Только смерть непоправима');
        insert into posts (id, author_id, is_blocked, is_deleted, time, title, post_text)
            values (13, 1, true, true, '2023-02-14 00:13:13', 'Aristotle', 'Quality is not an act, it is a habit');
        insert into posts (id, author_id, is_blocked, is_deleted, time, title, post_text)
            values (14, 1, true, false, '2023-07-18 18:53:00', 'Confucius', 'It does not matter how slowly you go as long as you do not stop');

        --Blocked user posts
        insert into posts (id, author_id, is_blocked, is_deleted, time, title, post_text)
            values (15, 4, true, true, '2023-09-11 17:20:17', 'Ницше Ф', 'У кого есть "Зачем", тот выдержит почти любое "Как"');
        insert into posts (id, author_id, is_blocked, is_deleted, time, title, post_text)
            values (16, 4, true, false, '2023-09-02 06:39:50', 'Nietzsche F', 'To live is to suffer, to survive is to find some meaning in the suffering');


        insert into post_comments (id, comment_text, is_blocked, is_deleted, time, author_id, post_id)
            values (100, 'IM FOOL!', true, false, '2023-04-19 08:42:27', 3, 10);
        insert into post_comments (id, comment_text, is_blocked, is_deleted, time, author_id, post_id)
            values (101, 'Great words!', true, true, '2023-03-30 05:24:19', 2, 10);
        insert into post_comments (id, comment_text, is_blocked, is_deleted, time, author_id, post_id)
            values (102, 'Awesome!', false, false, '2023-09-17 00:43:56', 1, 10);
        insert into post_comments (id, comment_text, is_blocked, is_deleted, time, author_id, post_id)
            values (103, 'Awesome!', false, false, '2023-09-17 00:43:56', 1, 11);
        insert into post_comments (id, comment_text, is_blocked, is_deleted, time, author_id, post_id)
            values (104, 'Parent comment text', false, false, '2023-09-17 00:43:56', 3, 12);
        insert into post_comments (id, comment_text, is_blocked, is_deleted, time, author_id, post_id, parent_id)
            values (106, 'Sub comment text', false, false, '2023-04-19 10:42:27', 2, 12, 104);


        --Blocked user comment
        insert into post_comments (id, comment_text, is_blocked, is_deleted, time, author_id, post_id)
            values (105, 'You FOOL!', true, true, '2023-03-14 22:30:18', 4, 10);


        insert into likes (id, entity_id, time, type, person_id)
            values (1000, 10, '2023-04-19 08:00:27', 'POST', 1);
        insert into likes (id, entity_id, time, type, person_id)
            values (1001, 10, '2022-04-19 08:01:27', 'POST', 2);
        insert into likes (id, entity_id, time, type, person_id)
            values (1002, 11, '2022-04-19 08:42:27', 'POST', 1);
        insert into likes (id, entity_id, time, type, person_id)
            values (1003, 100, '2022-04-19 08:42:27', 'COMMENT', 1);
        insert into likes (id, entity_id, time, type, person_id)
            values (1004, 101, '2020-04-19 08:42:27', 'COMMENT', 2);

        --Blocked person likes
        insert into likes (id, entity_id, time, type, person_id)
            values (1005, 10, '1920-04-19 08:42:27', 'POST', 4);
        insert into likes (id, entity_id, time, type, person_id)
            values (1006, 10, '1920-04-19 08:42:27', 'COMMENT', 4);
        insert into likes (id, entity_id, time, type, person_id)
            values (1007, 100, '1920-04-19 08:42:27', 'POST', 4);


        --Conclusion/Disclaimer:
        --  test data for comments and likes test has been created manually

        --Comments:
        --Post 10 has 4 comments:
        --  1 valid from user id=1,
        --  1 blocked from users id=3
        --  1 blocked and deleted from user id=2
        --  1 blocked and deleted from blocked and deleted user id=4
        --Post 11 has 2 comments:
        --  2 valid from user id=1, 3

        --Likes:
        --Post 10 has 3 likes:
        --  2 valid from user id=1, 2
        --  1 from blocked and deleted user id=4
        --Post 11 has 1 like:
        --  1 valid from user id=1
        --Comment 100 has 3 likes:
         -- 1 valid from user id=1,
        --  1 from blocked and deleted user id=4
        --  1 damaged record with wrong time
        --Comment 101 has 2 likes:
        --  1 valid from user id=2
        --  1 damaged record with wrong type