package main.model;

import lombok.Data;

import javax.persistence.*;

@Entity
@Table(name = "notification_type")
@Data
public class NotificationType {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "enum('POST', 'POST_COMMENT', 'COMMENT_COMMENT', 'FRIEND_REQUEST', 'MESSAGE', 'FRIEND_BIRTHDAY', 'LIKE')", nullable = false)
    private NotificationTypeCode code;

    @Column(nullable = false)
    private String name; // расшифрровка


}
