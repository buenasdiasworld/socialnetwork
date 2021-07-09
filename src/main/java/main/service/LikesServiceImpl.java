package main.service;


import lombok.RequiredArgsConstructor;
import main.core.ContextUtilities;
import main.data.request.LikeRequest;
import main.data.response.base.Response;
import main.data.response.type.LikesWithUsers;
import main.exception.BadRequestException;
import main.exception.apierror.ApiError;
import main.model.*;
import main.repository.BlocksBetweenUsersRepository;
import main.repository.LikesRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class LikesServiceImpl implements LikesService {
    private static final String COMMENT = "Comment";
    private static final String ERROR_NOT_POST_OR_COMMENT = "Выбран не пост или комментарий";
    private static final String ERROR_OBJECT_NOT_FOUND = "Объект не найден";
    private final LikesRepository likesRepository;
    private final PersonServiceImpl personService;
    private final CommentServiceImpl commentService;
    private final PostService postService;
    private final BlocksBetweenUsersRepository blocksBetweenUsersRepository;
    private final NotificationService notificationService;

    public Response<LikesWithUsers> isLiked(Integer userId, int itemId, String type) {
        Person person = userId == null ? personService.getAuthUser() : personService.getById(userId);
        Optional<Like> likeOptional;
        if (type.equals("Post") || type.equals(COMMENT)) {
            likeOptional = likesRepository.findByItemIdAndPersonIdAndType(itemId, person.getId(), LikeType.valueOf(type.toUpperCase()));
        } else {
            throw new BadRequestException(new ApiError(ERROR_NOT_POST_OR_COMMENT));
        }
        return new Response<>(new LikesWithUsers(likeOptional.isPresent() ? 1 : 0, Collections.emptyList()));
    }

    public Response<LikesWithUsers> getLikes(int itemId, String type) {
        personService.isAuthenticated();
        List<Like> likes = likesRepository.findAllByItemIdAndType(itemId, LikeType.valueOf(type.toUpperCase()));
        List<Integer> userIds = new ArrayList<>();
        likes.forEach(l -> userIds.add(l.getPerson().getId()));
        return new Response<>(new LikesWithUsers(likes.size(), userIds));
    }

    public Response<LikesWithUsers> setLike(LikeRequest request) {


        BlocksBetweenUsers blocksBetweenUsers = blocksBetweenUsersRepository
                .findBySrc_IdAndDst_Id(getUserIdByPostId(request.getItemId()), ContextUtilities.getCurrentUserId());
        if (!(blocksBetweenUsers == null)) {
            throw new BadRequestException(new ApiError("Access blocked", "Постановка лайка заблокирована"));
        }

        Person person = personService.getAuthUser();
        if (isLiked(person.getId(), request.getItemId(), request.getType()).getData().getLikes() == 0) {
            Like like = new Like();
            if (request.getType().equals("Post")) {
                Post post = postService.findById(request.getItemId());
                like.setItemId(post.getId());
                like.setType(LikeType.POST);
            } else if (request.getType().equals(COMMENT)) {
                PostComment comment = commentService.getComment(request.getItemId());
                like.setItemId(comment.getId());
                like.setType(LikeType.COMMENT);
            } else {
                throw new BadRequestException(new ApiError(ERROR_NOT_POST_OR_COMMENT));
            }
            like.setPerson(person);
            like.setTime(Instant.now());
            likesRepository.save(like);

            notificationService.setNotification(like);
        }
        return getLikes(request.getItemId(), request.getType());
    }

    @Transactional
    public Response<LikesWithUsers> deleteLike(int itemId, String type) {
        Person person = personService.getAuthUser();
        if (type.equals("Post") || type.equals(COMMENT)) {
            Like like = likesRepository.findByItemIdAndPersonIdAndType(itemId, person.getId(), LikeType.valueOf(type.toUpperCase()))
                    .orElseThrow(() -> new BadRequestException(new ApiError(ERROR_OBJECT_NOT_FOUND)));
            notificationService.deleteNotification(like);

            try {
                likesRepository.deleteByItemIdAndPersonIdAndType(itemId, person.getId(), LikeType.valueOf(type.toUpperCase()));
            } catch (Exception ex) {
                throw new BadRequestException(new ApiError(ERROR_OBJECT_NOT_FOUND));
            }
        } else {
            throw new BadRequestException(new ApiError(ERROR_NOT_POST_OR_COMMENT));
        }
        return getLikes(itemId, type);
    }

    public Like getLike(int itemId, LikeType type) {
        Optional<Like> likeOptional;
        if (type.equals(LikeType.POST) || type.equals(LikeType.COMMENT)) {
            likeOptional = likesRepository.findByItemIdAndType(itemId, type);
            if (likeOptional.isPresent()) {
                return likeOptional.get();
            } else {
                throw new BadRequestException(new ApiError(ERROR_OBJECT_NOT_FOUND));
            }
        } else {
            throw new BadRequestException(new ApiError(ERROR_NOT_POST_OR_COMMENT));
        }
    }

    @Override
    public Like findById(int id) {
        return likesRepository.findById(id)
                .orElseThrow(EntityNotFoundException::new);
    }

    private int getUserIdByPostId(int postId){
        return postService.findById(postId).getAuthor().getId();
    }
}
