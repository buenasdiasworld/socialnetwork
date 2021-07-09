package main.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.*;
import org.hibernate.annotations.WhereJoinTable;

import javax.persistence.*;
import java.time.Instant;
import java.util.List;

@Entity
@Table(name = "post")
@Data
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})

public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false)
    private Instant time;

    @JsonManagedReference
    @ManyToOne
    @JoinColumn(name = "author_id")
    private Person author;

    @Column(nullable = false)
    private String title;

    @Column(name = "post_text", columnDefinition = "text")
    private String postText;

    @Column(name = "is_blocked", nullable = false, columnDefinition = "TINYINT DEFAULT false")
    private boolean isBlocked = false;

    @JsonBackReference
    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PostTag> tags;

    @JsonBackReference
    @OneToMany
    @JoinTable(name = "likes", joinColumns = @JoinColumn(name = "itemId"), inverseJoinColumns = @JoinColumn(name = "id"))
    @WhereJoinTable(clause = "type = 'POST'")
    private List<Like> likes;

    @JsonBackReference
    @OneToMany(mappedBy = "post")
    private List<PostFile> files;
}
