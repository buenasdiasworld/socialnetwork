package main.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Table(name = "post2tag")
@Data
@NoArgsConstructor
public class PostTag {
    @EmbeddedId
    @EqualsAndHashCode.Exclude
    private PostTagId id = new PostTagId();

    @JsonManagedReference
    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("postId")
    private Post post;

    @JsonManagedReference
    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("tagId")
    private Tag tag;

    public PostTag(Post post, Tag tag) {
        this.post = post;
        this.tag = tag;
        id.setPostId(post.getId());
        id.setTagId(tag.getId());
    }
}
