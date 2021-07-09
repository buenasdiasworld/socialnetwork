alter table post_like rename post_file;

drop table if exists post_like;

create table post_like (
    id integer not null auto_increment,
    time datetime not null,
    person_id integer not null,
    post_id integer not null,
    primary key (id)
);
