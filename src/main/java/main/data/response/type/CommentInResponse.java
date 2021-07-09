package main.data.response.type;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import main.model.PostComment;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommentInResponse {
    @ApiModelProperty(value = "parent_id", example = "1")
    @JsonProperty(value = "parent_id")
    private Integer parentId;
    @JsonProperty(value = "comment_text")
    private String commentText;
    @ApiModelProperty(value = "id", example = "111")
    private long id;
    @JsonProperty(value = "post_id")
    private Integer postId;
    @ApiModelProperty(value = "time", example = "1559751301818")
    private long time;
    private PersonProfile author;
    @ApiModelProperty(value = "is_blocked", example = "false")
    @JsonProperty(value = "is_blocked")
    private boolean blocked;

    @JsonProperty("sub_comments")
    private List<CommentInResponse> subComments;

    @ApiModelProperty(value = "like_count", example = "10")
    @JsonProperty("like_count")
    private int likeCount;

    @ApiModelProperty(value = "is_my_like", example = "false")
    @JsonProperty("is_my_like")
    private boolean isMyLike;

    public CommentInResponse(PostComment comment, List<CommentInResponse> subComments, int userId) {
        parentId = comment.getParent() != null ? comment.getParent().getId() : 0;
        commentText = comment.getCommentText();
        id = comment.getId();
        postId = comment.getPost().getId();
        time = comment.getTime().toEpochMilli();
        author = new PersonProfile(comment.getAuthor());
        blocked = comment.isBlocked();
        this.subComments = subComments;
        if (comment.getLikes() != null) {
            likeCount = comment.getLikes() != null ? comment.getLikes().size() : 0;
            isMyLike = comment.getLikes().stream().anyMatch(l -> l.getPerson().getId() == userId);
        }
    }

    public CommentInResponse(PostComment comment, int userId) {
        parentId = comment.getParent() != null ? comment.getParent().getId() : 0;
        commentText = comment.getCommentText();
        id = comment.getId();
        postId = comment.getPost().getId();
        time = comment.getTime().toEpochMilli();
        author = new PersonProfile(comment.getAuthor());
        blocked = comment.isBlocked();
        subComments = new ArrayList<>();
        if (comment.getLikes() != null) {
            likeCount = comment.getLikes() != null ? comment.getLikes().size() : 0;
            isMyLike = comment.getLikes().stream().anyMatch(l -> l.getPerson().getId() == userId);
        }
    }
}
