SET FOREIGN_KEY_CHECKS = 0;
TRUNCATE TABLE `post_comment`;
TRUNCATE TABLE `notification`;
SET FOREIGN_KEY_CHECKS = 1;


INSERT INTO post_comment (id, comment_text, is_blocked, time, author_id, parent_id, post_id)
    VALUES (1, "first comment to the post", 0, NOW(), 2, null, 1),
        (2, "second comment to the post", 0, NOW(), 1, 1, 1),
        (3, "third comment to the post", 0, NOW(), 1, null, 2),
        (4, "fouth comment to the post", 0, NOW(), 1, null, 3);

INSERT INTO notification (id, contact, entity_id, person_id, sent_time, type_id, read_status)
    VALUES (1, "***", 1, 1, NOW(), 2, "SENT");