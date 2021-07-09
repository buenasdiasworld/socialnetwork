package main.model;

import lombok.Data;

import javax.persistence.*;

@Entity
@Table(name = "blocks_between_users")
@Data
public class BlocksBetweenUsers {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @ManyToOne
    @JoinColumn(name = "src_person_id")
    private Person src;
    @ManyToOne
    @JoinColumn(name = "dst_person_id")
    private Person dst;
}
