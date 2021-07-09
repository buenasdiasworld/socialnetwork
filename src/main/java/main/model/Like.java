package main.model;

import lombok.Data;

import javax.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "likes")
@Data
public class Like {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false)
    private int itemId;

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "enum('POST', 'COMMENT')", nullable = false)
    private LikeType type;

    @OneToOne
    @JoinColumn(name = "person_id")
    private Person person;

    @Column(nullable = false)
    private Instant time;
}
