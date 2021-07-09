alter table notification_type MODIFY COLUMN code enum('POST','POST_COMMENT','COMMENT_COMMENT','FRIEND_REQUEST','MESSAGE', 'FRIEND_BIRTHDAY');
insert into notification_type (id,code,name) values (1,"POST","новый пост");
insert into notification_type (id,code,name) values (2,"POST_COMMENT","комментарий к посту");
insert into notification_type (id,code,name) values (3,"COMMENT_COMMENT","ответ на комментарий");
insert into notification_type (id,code,name) values (4,"FRIEND_REQUEST","запрос дружбы");
insert into notification_type (id,code,name) values (5,"MESSAGE","личное сообщение");
insert into notification_type (id,code,name) values (6,"FRIEND_BIRTHDAY","день рождения друга");

alter table notification add column read_status varchar(255) not null;

drop table if exists notification_settings;

create table notification_settings (
    id integer not null auto_increment,
    person_id integer not null,
    is_enabled TINYINT not null,
    notification_type_id integer not null,
    primary key (id)

);

alter table notification_settings add constraint fk_person_settings_id  foreign key (person_id) references person(id);
