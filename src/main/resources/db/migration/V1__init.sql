drop table if exists block_history;
drop table if exists friendship;
drop table if exists friendship_status;
drop table if exists message;
drop table if exists notification;
drop table if exists notification_type;
drop table if exists person;
drop table if exists post;
drop table if exists post2tag;
drop table if exists post_comment;
drop table if exists post_like;
drop table if exists tag;
drop table if exists user;

create table block_history (
    id integer not null auto_increment,
    action enum('BLOCK', 'UNBLOCK') not null,
    comment_id integer not null,
    person_id integer not null,
    post_id integer not null,
    time datetime not null,
    primary key (id)
);

create table friendship (
    id integer not null auto_increment,
    dst_person_id integer not null,
    src_person_id integer not null,
    status_id integer not null,
    primary key (id)
);

create table friendship_status (
    id integer not null auto_increment,
    code enum('REQUEST', 'FRIEND', 'BLOCKED', 'DECLINED', 'SUBSCRIBED') not null,
    name varchar(255) not null,
    time datetime not null,
    primary key (id)
);

create table message (
    id integer not null auto_increment,
    author_id integer not null,
    message_text text,
    read_status enum('SENT', 'READ') not null,
    recipient_id integer not null,
    time datetime not null,
    primary key (id)
);

create table notification (
    id integer not null auto_increment,
    contact varchar(255) not null,
    entity_id integer not null,
    person_id integer not null,
    sent_time datetime not null,
    type_id integer not null,
    primary key (id)
);

create table notification_type (
    id integer not null auto_increment,
    code enum('POST', 'POST_COMMENT', 'COMMENT_COMMENT', 'FRIEND_REQUEST', 'MESSAGE') not null,
    name varchar(255) not null,
    primary key (id)
);

create table person (
    id integer not null auto_increment,
    about text,
    birth_date date,
    confirmation_code varchar(255),
    e_mail varchar(255) not null,
    first_name varchar(255) not null,
    phone varchar(255),
    is_approved TINYINT not null,
    is_blocked TINYINT not null,
    last_name varchar(255) not null,
    last_online_time datetime,
    messages_permission enum('ALL', 'FRIENDS') not null,
    password varchar(255) not null,
    photo text,
    reg_date datetime not null,
    town varchar(255),
    primary key (id)
);

create table post (
    id integer not null auto_increment,
    author_id integer not null,
    is_blocked TINYINT not null,
    post_text text,
    time datetime not null,
    title varchar(255),
    primary key (id)
);

create table post2tag (
    id integer not null auto_increment,
    post_id integer not null,
    tag_id integer not null,
    primary key (id)
);

create table post_comment (
    id integer not null auto_increment,
    author_id integer not null,
    comment_text text,
    is_blocked TINYINT not null,
    parent_id integer not null,
    post_id integer not null,
    time datetime not null,
    primary key (id)
);

create table post_like (
    id integer not null auto_increment,
    name varchar(255) not null,
    path varchar(255) not null,
    post_id integer not null,
    primary key (id)
);

create table tag (
    id integer not null auto_increment,
    tag varchar(255) not null,
    primary key (id)
);

create table user (
    id integer not null auto_increment,
    e_mail varchar(255) not null,
    name varchar(255) not null,
    password varchar(255) not null,
    type enum('MODERATOR', 'ADMIN') not null,
    primary key (id)
);
