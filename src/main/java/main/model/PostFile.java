package main.model;

import lombok.Data;

import javax.persistence.*;

@Entity
@Table(name = "post_file")
@Data
public class PostFile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name = "post_id")
    private Post post;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String path;
}
