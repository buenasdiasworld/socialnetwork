package main.model;

import lombok.Data;

import javax.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "message")
@Data
public class Message {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false)
    private Instant time;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "dialog_id", referencedColumnName = "id", nullable = false)
    private Dialog dialog;

    @ManyToOne
    @JoinColumn(name = "author_id")
    private Person author;

    @ManyToOne
    @JoinColumn(name = "recipient_id")
    private Person recipient;

    @Column(columnDefinition = "text")
    private String messageText;

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "enum('SENT', 'READ')", nullable = false)
    private ReadStatus readStatus;
}
