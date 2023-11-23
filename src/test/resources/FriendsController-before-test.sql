insert into person_settings (comment_comment, friend_birthday, friend_request, post_like, message, post_comment, post) values (false, false, true, false, false, false, true);
insert into person_settings (comment_comment, friend_birthday, friend_request, post_like, message, post_comment, post) values (true, true, false, false, false, false, true);
insert into person_settings (comment_comment, friend_birthday, friend_request, post_like, message, post_comment, post) values (true, false, false, false, true, true, false);
insert into person_settings (comment_comment, friend_birthday, friend_request, post_like, message, post_comment, post) values (false, false, true, false, false, false, true);
insert into person_settings (comment_comment, friend_birthday, friend_request, post_like, message, post_comment, post) values (false, true, true, true, false, false, true);
insert into person_settings (comment_comment, friend_birthday, friend_request, post_like, message, post_comment, post) values (true, false, true, false, false, false, true);

insert into persons (first_name, last_name, e_mail, phone, reg_date, birth_date, city, country, deleted_time, last_online_time, is_approved, is_blocked, is_deleted, person_settings_id, password, message_permissions, online_status) values ('Ella', 'Chaplin', 'echaplin0@thetimes.co.uk', '+505-472-972-1137', '2023-07-26 22:25:20', '2006/08/28', 'Las Sabanas', 'Nicaragua', null, '2023-09-15 17:06:48', false, true, true, 1, 'MSFnNmswZjQ=', 'ALL', false);
insert into persons (first_name, last_name, e_mail, phone, reg_date, birth_date, city, country, deleted_time, last_online_time, is_approved, is_blocked, is_deleted, person_settings_id, password, message_permissions, online_status) values ('Adriaens', 'Whillock', 'awhillock1@gov.uk', '+48-832-911-0867', '2023-01-02 18:51:39', '1989/05/26', 'Dobczyce', 'Poland', null, '2023-08-01 00:59:05', false, true, false, 2, 'MSFnNmswZjQ=', 'ALL', false);
insert into persons (first_name, last_name, e_mail, phone, reg_date, birth_date, city, country, deleted_time, last_online_time, is_approved, is_blocked, is_deleted, person_settings_id, password, message_permissions, online_status) values ('Ailsun', 'Asbury', 'aasbury2@paginegialle.it', '+46-328-795-3579', '2023-06-13 19:02:09', '2008/02/24', 'Stockholm', 'Sweden', null, null, false, false, false, 3, 'MSFnNmswZjQ=', 'ALL', false);
insert into persons (first_name, last_name, e_mail, phone, reg_date, birth_date, city, country, deleted_time, last_online_time, is_approved, is_blocked, is_deleted, person_settings_id, password, message_permissions, online_status) values ('Kerr', 'Morena', 'kmorena3@nationalgeographic.com', '+381-453-175-4645', '2023-05-26 09:22:42', '2015/02/15', 'Trstenik', 'Serbia', null, '2023-09-25 05:04:56', false, true, false, 4, 'MSFnNmswZjQ=', 'ALL', false);
insert into persons (first_name, last_name, e_mail, phone, reg_date, birth_date, city, country, deleted_time, last_online_time, is_approved, is_blocked, is_deleted, person_settings_id, password, message_permissions, online_status) values ('Thaxter', 'Bartlet', 'tbartlet4@gizmodo.com', '+55-679-336-6239', '2023-08-15 09:57:53', '1980/01/22', 'Campos Gerais', 'Brazil', null, '2023-09-09 11:58:22', false, true, true, 5, 'MSFnNmswZjQ=', 'ALL', false);
insert into persons (first_name, last_name, e_mail, phone, reg_date, birth_date, city, country, deleted_time, last_online_time, is_approved, is_blocked, is_deleted, person_settings_id, password, message_permissions, online_status) values ('Angela', 'Borrowman', 'aborrowman5@vkontakte.ru', '+54-738-303-0295', '2023-03-08 12:06:41', '2001/01/30', 'La Puerta de San José', 'Argentina', '2023/08/24', null, true, false, false, 6, 'MSFnNmswZjQ=', 'ALL', false);

insert into friendships (sent_time, status_name, src_person_id, dst_person_id) values
                                                                                   ('2023-09-22 22:01:13', 'FRIEND', 3, 1),
                                                                                   ('2023-09-22 22:01:13', 'FRIEND', 1, 3),
                                                                                   ('2023-09-07 22:24:34', 'RECEIVED_REQUEST', 1, 4),
                                                                                   ('2023-09-07 22:24:34', 'REQUEST', 4, 1),
                                                                                   ('2023-09-07 22:24:34', 'RECEIVED_REQUEST', 2, 3),
                                                                                   ('2023-09-15 13:12:14', 'REQUEST', 5, 1),
                                                                                   ('2023-09-15 13:12:15', 'RECEIVED_REQUEST', 1, 5),
                                                                                   ('2023-09-15 13:12:15', 'REQUEST', 1, 6);
