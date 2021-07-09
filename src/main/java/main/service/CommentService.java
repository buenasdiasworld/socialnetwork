package main.service;

import main.data.request.CommentRequest;
import main.data.response.CommentResponse;
import main.data.response.base.ListResponse;
import main.data.response.base.Response;
import main.data.response.type.CommentInResponse;
import main.data.response.type.ItemDelete;
import main.model.Post;
import main.model.PostComment;

import java.util.List;

public interface CommentService {
    CommentResponse createComment(Integer postId, CommentRequest request);

    ListResponse<CommentInResponse> getPostComments(Integer postId, Integer offset, Integer itemPerPage);

    List<CommentInResponse> getCommentsList(List<Post> posts);

    CommentResponse editComment(Integer id, Integer commentId, CommentRequest request);

    Response<ItemDelete> deleteComment(Integer postId, Integer commentId);

    void deleteAllComments(Integer postId);

    PostComment getComment(int itemId);

    PostComment findById(int id);

    List<PostComment> findAllByPostId(int postId);

    List<PostComment> subComments (PostComment comment);
}
