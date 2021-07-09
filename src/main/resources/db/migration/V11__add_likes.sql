alter table post_like rename likes;
alter table likes drop column post_id;
alter table likes add column item_id integer not null;
alter table likes add column type enum('POST', 'COMMENT') not null;
