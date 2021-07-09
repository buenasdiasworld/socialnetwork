package main.data.response.type;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import main.model.Post;
import main.model.PostType;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PostInResponse {
    private Integer id;
    @ApiModelProperty(value = "time", example = "1559751301818")
    private long time;
    private PersonProfile author;
    @ApiModelProperty(value = "title", example = "Заголовок поста")
    private String title;
    @ApiModelProperty(value = "post_text", example = "Текст поста")
    @JsonProperty(value = "post_text")
    private String postText;
    @JsonProperty(value = "is_blocked")
    private boolean isBlocked;
    private int likes;
    private List<CommentInResponse> comments;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private PostType type;
    @ApiModelProperty(name = "tags", dataType = "List", example = "[\"lol\", \"IT\", \"post\"]")
    private List<String> tags;
    @JsonProperty(value = "my_like")
    private boolean myLike;

    public PostInResponse(Post post, List<CommentInResponse> commentsList, int currentUserId) {
        id = post.getId();
        time = post.getTime().getEpochSecond();
        author = new PersonProfile(post.getAuthor());
        title = post.getTitle();
        postText = post.getPostText();
        isBlocked = post.isBlocked();
        likes = post.getLikes() != null ? post.getLikes().size() : 0;
        comments = commentsList.stream()
                .filter(commentInResponse -> commentInResponse.getPostId().equals(id))
                .collect(Collectors.toList());
        tags = getTags(post);
        myLike = likes != 0 && post.getLikes().stream().anyMatch(l -> l.getPerson().getId() == currentUserId);
    }

    private List<String> getTags(Post post) {
        List<String> postTags = new ArrayList<>();
        post.getTags().forEach(t -> postTags.add(t.getTag().getTag()));
        return postTags;
    }
}
