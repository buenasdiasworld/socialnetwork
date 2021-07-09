drop table if exists blocks_between_users;

create table blocks_between_users (
     id integer not null auto_increment,
     dst_person_id integer not null,
     src_person_id integer not null,
     primary key (id)
);