DELETE FROM `post`;

INSERT INTO `post` (id, author_id, is_blocked, post_text, time, title)
    VALUES
        (1, 1, 0, "new post's text", NOW(), "Hello, Post One"),
        (2, 1, 0, "new post's text", NOW(), "Hello, Post Two"),
        (3, 1, 0, "new post's text", NOW(), "Hello, Post Three"),
        (4, 1, 0, "new post's text", NOW(), "Hello, Post Four"),
        (5, 3, 0, "new post's text", '2020-11-20 19:04:18', "Hello, Post Five"),
        (6, 4, 0, "new post's text", '2020-11-20 19:04:18', "Hello, Post Six");

         INSERT INTO `tag` (id, tag) VALUES ('1', 'tag1'), ('2', 'tag2');
         INSERT INTO `post2tag` (post_id, tag_id) VALUES ('6', '1'),('6', '2');