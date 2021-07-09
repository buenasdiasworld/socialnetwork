alter table person add constraint fk_person_country_id foreign key (country_id) references country (id);
alter table person add constraint fk_person_city_id foreign key (city_id) references city (id);

