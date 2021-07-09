drop table if exists language;

create table language (
   id integer not null auto_increment,
   title varchar(255) not null,
   primary key (id)
);

insert into language (title) values('Русский');
insert into language (title) values('English');
insert into language (title) values('Deutsch');
