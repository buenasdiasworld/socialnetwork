alter table dialog add column primary_recipient_id integer;
alter table dialog add constraint fk_dialog_primary_recipient_id foreign key (primary_recipient_id) references person (id);
