drop table if exists country;
drop table if exists city;

create table country (
   id integer not null auto_increment,
   title varchar(255) not null,
   primary key (id)
);

create table city (
   id integer not null auto_increment,
   country_id integer not null,
   title varchar(255) not null,
   primary key (id)
);

alter table city add constraint fk_city_country_id foreign key (country_id) references country (id);
alter table person drop town;
alter table person add column city_id integer;
alter table person add column country_id integer;
