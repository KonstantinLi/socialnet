
delete from friendships;
delete from persons;
delete from person_settings;

alter sequence friendships_id_seq restart with 1;
alter sequence person_settings_id_seq restart with 1;
alter sequence persons_id_seq restart with 1;