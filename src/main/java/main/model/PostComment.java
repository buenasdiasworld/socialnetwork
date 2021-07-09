package main.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.Data;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.time.Instant;
import java.util.List;

@Entity
@Table(name = "post_comment")
@Data
public class PostComment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false)
    private Instant time;

    @ManyToOne
    @JoinColumn(name = "post_id")
    private Post post;

    @ManyToOne
    @JoinColumn(name = "parent_id")
    private PostComment parent;

    @ManyToOne
    @JoinColumn(name = "author_id")
    private Person author;

    @Column(columnDefinition = "text")
    private String commentText;

    @Column(nullable = false, columnDefinition = "TINYINT")
    private boolean isBlocked = false;

    @JsonManagedReference
    @OneToMany
    @JoinTable(name = "likes", joinColumns = @JoinColumn(name = "itemId"), inverseJoinColumns = @JoinColumn(name = "id"))
    @Where(clause = "type = 'COMMENT'")
    private List<Like> likes;
}
