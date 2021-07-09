alter table dialog drop foreign key fk_dialog_primary_recipient_id;
alter table dialog drop column primary_recipient_id;
alter table dialog add column name varchar(255);
alter table dialog add column is_group TINYINT not null;
