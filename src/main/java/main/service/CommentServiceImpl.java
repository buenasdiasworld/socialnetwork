package main.service;

import lombok.RequiredArgsConstructor;
import main.core.ContextUtilities;
import main.data.request.CommentRequest;
import main.data.response.CommentResponse;
import main.data.response.base.ListResponse;
import main.data.response.base.Response;
import main.data.response.type.CommentInResponse;
import main.data.response.type.ItemDelete;
import main.exception.BadRequestException;
import main.exception.apierror.ApiError;
import main.model.BlocksBetweenUsers;
import main.model.Person;
import main.model.Post;
import main.model.PostComment;
import main.repository.BlocksBetweenUsersRepository;
import main.repository.PostCommentRepository;
import main.repository.PostRepository;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {
    private static final String INVALID_REQUEST = "invalid request";

    private final PostCommentRepository commentRepository;
    private final PostRepository postRepository;
    private final PersonService personService;
    private final BlocksBetweenUsersRepository blocksBetweenUsersRepository;
    private final NotificationService notificationService;

    @Override
    public CommentResponse createComment(Integer postId, CommentRequest request) {


        BlocksBetweenUsers blocksBetweenUsers = blocksBetweenUsersRepository
                .findBySrc_IdAndDst_Id(getUserIdByPostId(postId), ContextUtilities.getCurrentUserId());
        if (!(blocksBetweenUsers == null)) {
            throw new BadRequestException(new ApiError("Access blocked", "Написание комментария заблокировано"));
        }

        if (request.getCommentText().isBlank()) {
            throw new BadRequestException(new ApiError(INVALID_REQUEST, "текст комментария отсутствует"));
        }
        CommentResponse response = new CommentResponse();

        PostComment postComment = new PostComment();
        postComment.setCommentText(request.getCommentText());
        if (request.getParentId() != null) {
            Optional<PostComment> postCommentOpt = commentRepository.findById(request.getParentId());
            postCommentOpt.ifPresent(postComment::setParent);
        }

        Person currentUser = personService.getAuthUser();
        postComment.setAuthor(currentUser);
        postComment.setTime(Instant.now());
        postComment.setBlocked(false);
        postComment.setPost(postRepository.findById(postId)
                .orElseThrow(() -> new BadRequestException(
                        new ApiError(INVALID_REQUEST, "ссылка на несуществующий пост")
                )));
        commentRepository.save(postComment);

        //создаем notification
        notificationService.setNotification(postComment);
        CommentInResponse commentInResponse = new CommentInResponse(postComment, new ArrayList<>(), currentUser.getId());
        response.setData(commentInResponse);
        return response;
    }

    @Override
    public ListResponse<CommentInResponse> getPostComments(Integer postId, Integer offset, Integer itemPerPage) {
        Person currentUser = personService.getAuthUser();
        List<PostComment> comments = commentRepository.findAllByPostId(postId);
        List<CommentInResponse> list = getComments(comments, currentUser);
        return new ListResponse<>(list, list.size(), offset, itemPerPage);
    }

    private List<CommentInResponse> getComments(List<PostComment> comments, Person currentUser) {
        List<CommentInResponse> commentsDto = comments.stream().map(p -> new CommentInResponse(p, currentUser.getId())).collect(Collectors.toList());
        List<CommentInResponse> commentsResult = commentsDto.stream()
                .filter(commentInResponse -> commentInResponse.getParentId() == 0).collect(Collectors.toList());
        for (CommentInResponse commentDto : commentsResult) {
            List<CommentInResponse> subComments = getSublistComment(commentsDto, commentDto.getId());
            commentDto.setSubComments(subComments);
        }
        return commentsResult;
    }

    private List<CommentInResponse> getSublistComment(List<CommentInResponse> comments, long commentId) {
        List<CommentInResponse> subComments = new ArrayList<>();

        for (CommentInResponse comment : comments) {
            if (comment.getParentId() == commentId) {
                subComments.add(comment);
            }
        }

        return subComments;
    }

    @Override
    public List<CommentInResponse> getCommentsList(List<Post> posts) {
        Person currentUser = personService.getAuthUser();
        Set<Integer> list = posts.stream().map(Post::getId).collect(Collectors.toSet());
        List<PostComment> comments = commentRepository.getCommentsByList(list);
        return getComments(comments, currentUser);
    }

    @Override
    public CommentResponse editComment(Integer id, Integer commentId, CommentRequest request) {
        CommentResponse response = new CommentResponse();

        PostComment comment = getComment(commentId);

        comment.setCommentText(request.getCommentText());
        commentRepository.save(comment);
        return response;
    }

    @Override
    public Response<ItemDelete> deleteComment(Integer postId, Integer commentId) {
        Response<ItemDelete> response = new Response<>();
        ItemDelete item = new ItemDelete();
        item.setId(commentId);

        PostComment comment = getComment(commentId);
        notificationService.deleteNotification(comment);
        deleteSubComment(commentId);
        commentRepository.delete(comment);
        response.setData(item);
        return response;
    }

    private void deleteSubComment(Integer commentId) {
        List<PostComment> subComments = commentRepository.subCommentsG(commentId);
        commentRepository.deleteAll(subComments);
    }

    @Override
    public void deleteAllComments(Integer postId) {
        commentRepository.deleteAllByPostId(postId);
    }

    @Override
    public PostComment getComment(int itemId) {
        Optional<PostComment> optionalPostComment = commentRepository.findById(itemId);
        if (optionalPostComment.isPresent()) {
            return optionalPostComment.get();
        } else {
            throw new BadRequestException(new ApiError(INVALID_REQUEST, "Несуществующий коммент"));
        }
    }

    @Override
    public PostComment findById(int id) {
        return commentRepository.findById(id)
                .orElseThrow(EntityNotFoundException::new);
    }

    @Override
    public List<PostComment> findAllByPostId(int postId) {
        return commentRepository.findAllByPostId(postId);
    }

    @Override
    public List<PostComment> subComments(PostComment comment){
        return new ArrayList<>(commentRepository.subCommentsG(comment.getId()));
    }


    private int getUserIdByPostId(int postId){
        return postRepository.findById(postId).get().getAuthor().getId();
    }
}

