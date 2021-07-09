package main.model;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.time.Instant;

@Entity
@Table(name = "notification")
@Data
public class Notification implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @OneToOne
    @JoinColumn(name = "type_id")
    private NotificationType type;

    @Column(name = "sent_time", nullable = false)
    private Instant sentTime;

    @ManyToOne
    @JoinColumn(name = "person_id")
    private Person receiver;

    @Column(name = "entity_id")
    private int entityId;

    @Column(nullable = false)
    private String contact;

    @Enumerated(EnumType.STRING)
    @Column(name = "read_status", columnDefinition = "enum('SENT', 'READ')", nullable = false)
    private NotificationReadStatusCode readStatus;

    public Notification() {
        sentTime = Instant.now();
        contact = "";
        readStatus = NotificationReadStatusCode.SENT;
    }
}
