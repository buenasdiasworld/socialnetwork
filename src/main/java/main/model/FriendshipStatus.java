package main.model;

import lombok.Data;

import javax.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "friendship_status")
@Data
public class FriendshipStatus {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false)
    private Instant time;

    @Column(nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "enum('REQUEST', 'FRIEND', 'BLOCKED', 'DECLINED', 'SUBSCRIBED')", nullable = false)
    private FriendshipStatusCode code;
}
