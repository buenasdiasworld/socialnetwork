SET FOREIGN_KEY_CHECKS = 0;
TRUNCATE TABLE `person`;
TRUNCATE TABLE `city`;
TRUNCATE TABLE `country`;
TRUNCATE TABLE `notification_type`;
TRUNCATE TABLE `notification_settings`;
SET FOREIGN_KEY_CHECKS = 1;

INSERT INTO country (id, title) VALUES (1, "Россия");
INSERT INTO city (id, country_id, title) VALUES (1, 1, "Москва");

INSERT INTO `person` (
    id, about, birth_date, confirmation_code, e_mail, first_name, phone,
    is_approved, is_blocked, last_name, last_online_time,
    messages_permission, password, photo, reg_date,
    city_id, country_id, is_deleted)
    VALUES (1, "about", "2020-10-22", "referer", "user@user.ru", "testUser",
    "111-222-3333", 1, 0, "LastName", NOW(), "ALL", "$2a$10$CogjAlX0O78H9AbuJDsBMuExLksckVOlx/Y9b/8Gv0FDS0Tjd2zna",
    "/static/img/default_avatar.png", NOW(), 1, 1, 0);
INSERT INTO `person` (
    id, about, birth_date, confirmation_code, e_mail, first_name, phone,
    is_approved, is_blocked, last_name, last_online_time,
    messages_permission, password, photo, reg_date,
    city_id, country_id, is_deleted)
    VALUES (2, "about", "2010-10-22", "", "user01@user.ru", "testUser01",
    "111-222-4444", 1, 0, "LastName01", NOW(), "ALL", "$2a$10$CogjAlX0O78H9AbuJDsBMuExLksckVOlx/Y9b/8Gv0FDS0Tjd2zna",
    "/static/img/default_avatar.png", NOW(), 1, 1, 0);

INSERT INTO `person` (
            id, about, birth_date, confirmation_code, e_mail, first_name, phone,
            is_approved, is_blocked, last_name, last_online_time,
            messages_permission, password, photo, reg_date,
            city_id, country_id, is_deleted)
    VALUES (3, "about", "2008-10-22", "", "user02@user.ru", "Иван",
    "111-222-4444", 1, 0, "LastName01", NOW(), "ALL", "$2a$10$CogjAlX0O78H9AbuJDsBMuExLksckVOlx/Y9b/8Gv0FDS0Tjd2zna",
    "/static/img/default_avatar.png", NOW(), 1, 1, 0);

INSERT INTO `person` (
              id, about, birth_date, confirmation_code, e_mail, first_name, phone,
              is_approved, is_blocked, last_name, last_online_time,
              messages_permission, password, photo, reg_date,
              city_id, country_id, is_deleted)
    VALUES (4, "about", "2009-10-22", "", "user03@user.ru", "Иван",
    "111-222-4444", 1, 0, "LastName01", NOW(), "ALL", "$2a$10$CogjAlX0O78H9AbuJDsBMuExLksckVOlx/Y9b/8Gv0FDS0Tjd2zna",
    "/static/img/default_avatar.png", NOW(), 1, 1, 0);

INSERT INTO notification_type (id, code, name) VALUES (1, "POST", "новый пост"),
    (2, "POST_COMMENT", "новый комментарий к посту"),
    (3, "COMMENT_COMMENT", "новый комментарий к комментарию"),
    (4, "LIKE", "лайк");

INSERT INTO `notification_settings` (
    id, person_id, is_enabled, notification_type_id) VALUES
    (1, 1, 0, 1),
    (2, 1, 1, 2),
    (3, 1, 0, 3),
    (4, 1, 1, 4);
