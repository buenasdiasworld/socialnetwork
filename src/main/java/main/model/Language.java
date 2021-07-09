package main.model;

import lombok.Data;

import javax.persistence.*;

@Entity
@Table(name = "language")
@Data
public class Language {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false)
    private String title;
}
