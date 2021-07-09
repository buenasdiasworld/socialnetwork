package main.model;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.time.Instant;

@Entity
@Table(name = "block_history")
@Data
public class BlockHistory implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false)
    private Instant time;

    @ManyToOne
    @JoinColumn(name = "person_id")
    private Person person;

    @OneToOne
    @JoinColumn(name = "post_id")
    private Post post;

    @OneToOne
    @JoinColumn(name = "comment_id")
    private PostComment comment;

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "enum('BLOCK', 'UNBLOCK')", nullable = false)
    private ActionType action;
}
