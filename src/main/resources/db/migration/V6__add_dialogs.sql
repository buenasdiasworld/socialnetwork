drop table if exists dialog;
drop table if exists dialog2person;

create table dialog (
     id integer not null auto_increment,
     primary key (id)
);

create table dialog2person (
      id integer not null auto_increment,
      dialog_id integer not null,
      person_id integer not null,
      primary key (id)
);

alter table dialog2person add constraint fk_dialog2person_dialog_id foreign key (dialog_id) references dialog (id);
alter table dialog2person add constraint fk_dialog2person_person_id foreign key (person_id) references person (id);

alter table message add column dialog_id integer not null;
alter table message add constraint fk_message_dialog_id foreign key (dialog_id) references dialog (id);
