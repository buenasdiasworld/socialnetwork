package main.model;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "friendship")
@Data
public class Friendship implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @OneToOne
    @JoinColumn(name = "status_id")
    private FriendshipStatus status;

    @ManyToOne
    @JoinColumn(name = "src_person_id")
    private Person src;

    @ManyToOne
    @JoinColumn(name = "dst_person_id")
    private Person dst;
}
