package main.service;

import lombok.RequiredArgsConstructor;
import main.core.ContextUtilities;
import main.core.OffsetPageRequest;
import main.data.request.PostRequest;
import main.data.response.base.ListResponse;
import main.data.response.base.Response;
import main.data.response.type.CommentInResponse;
import main.data.response.type.PostDelete;
import main.data.response.type.PostInResponse;
import main.exception.BadRequestException;
import main.exception.apierror.ApiError;
import main.model.*;
import main.repository.BlocksBetweenUsersRepository;
import main.repository.PostRepository;
import main.repository.PostTagRepository;
import main.repository.TagRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {
    private static final String INVALID_REQUEST = "invalid_request";

    private final PostRepository postRepository;
    private final TagRepository tagRepository;
    private final PostTagRepository postTagRepository;
    private final CommentService commentService;
    private final PersonService personService;
    private final NotificationService notificationService;
    private final BlocksBetweenUsersRepository blocksBetweenUsersRepository;

    @Override
    public ListResponse<PostInResponse> getFeeds(String name, int offset, int itemPerPage) {
        Pageable pageable = new OffsetPageRequest(offset, itemPerPage, Sort.by("time").descending());
        Page<Post> postPage;
        if (name != null && !name.isEmpty()) {
            postPage = postRepository.findByTitle(name, pageable);
        } else {
            postPage = postRepository.findAll(pageable);
        }
        List<Post> postsList = cleanBlockedPosts(postPage);
        List<CommentInResponse> commentsList = commentService.getCommentsList(postsList);
        return new ListResponse<>(extractPostList(postsList, commentsList), postsList.size(), offset, itemPerPage);
    }

    @Override
    @Transactional
    public Response<PostInResponse> addNewPost(Integer personId, PostRequest request, Long pubDate) {
        try {
            Person person = personService.getById(personId);
            Post post = savePost(null, request, person, pubDate);

            notificationService.setNotification(post);

            return new Response<>(new PostInResponse(post, new ArrayList<>(), personId));
        } catch (Exception ex) {
            throw new BadRequestException(new ApiError(INVALID_REQUEST, "Ошибка создания поста"));
        }
    }

    @Override
    @Transactional
    public Response<PostInResponse> editPost(int id, Long pubDate, PostRequest request) {
        Post post = findById(id);

        Person person = personService.getAuthUser();
        post = savePost(post, request, person, pubDate);

        return new Response<>(new PostInResponse(post, new ArrayList<>(), person.getId()));

    }

    @Override
    @Transactional
    public Response<PostDelete> delPost(Integer id) {
        try {
            Post post = findById(id);
            notificationService.deleteNotification(post);
            commentService.deleteAllComments(post.getId());
            postRepository.delete(post);
            return new Response<>(new PostDelete(id));
        } catch (BadRequestException ex) {
            throw new BadRequestException(new ApiError(INVALID_REQUEST, "Ошибка удаления поста"));
        }
    }

    @Override
    public ListResponse<PostInResponse> showWall(Integer personId, int offset, int itemsPerPage) {
        personService.isAuthenticated();
        Person person = personService.getById(personId);
        Pageable pageable = new OffsetPageRequest(offset, itemsPerPage, Sort.by("time").descending());
        Page<Post> postPage = postRepository.findByAuthor(person, pageable);
        List<CommentInResponse> commentsList = commentService.getCommentsList(postPage.getContent());
        return new ListResponse<>(extractPage(postPage, commentsList), postPage.getTotalElements(), offset, itemsPerPage);
    }

    //-----------------------
    private Post savePost(Post post, PostRequest postData, Person person, Long pubDate) {
        Post postToSave = (post == null) ? new Post() : post;

        //sonar fixes change ternary operation into independent statement
        final Instant postTime;
        if (pubDate == null || Instant.ofEpochMilli(pubDate).isBefore(Instant.now())) {
            postTime = Instant.now();
        } else {
            postTime = Instant.ofEpochMilli(pubDate);
        }

        final List<PostTag> postTags = (post == null) ? new ArrayList<>() : postToSave.getTags();
        final List<Tag> tags = new ArrayList<>();
        for (String s : postData.getTags()) {
            Optional<Tag> optionalTag = tagRepository.findTagByTag(s);
            Tag tag;
            if (optionalTag.isEmpty()) {
                tag = new Tag();
                tag.setTag(s.trim());
            } else {
                tag = optionalTag.get();
            }
            tags.add(tag);
            PostTag postTag = new PostTag(postToSave, tag);
            if (!postTags.contains(postTag)) {
                postTags.add(postTag);
            }
        }
        postTags.removeIf(pt -> !tags.contains(pt.getTag()));
        postToSave.setTitle(postData.getTitle());
        postToSave.setPostText(postData.getPostText());
        postToSave.setTags(postTags);
        postToSave.setTime(postTime);
        postToSave.setAuthor(person);
        tagRepository.saveAll(tags);
        postToSave = postRepository.save(postToSave);
        postTagRepository.saveAll(postTags);
        return postToSave;
    }

    @Override
    public Post getPost(int id) {
        Optional<Post> postOptional = postRepository.findById(id);
        if (postOptional.isEmpty()) {
            throw new BadRequestException(new ApiError(INVALID_REQUEST, "Пост не существует"));
        }
        return postOptional.get();
    }

    @Override
    public Post findById(int id) {
        return postRepository.findById(id).orElseThrow(
                () -> new BadRequestException(new ApiError(
                        INVALID_REQUEST,
                        "Пост не существует")));
    }

    @Override
    public List<Post> findByAuthor(Person author) {
        return postRepository.findByAuthor(author);
    }

    private List<PostInResponse> extractPage(Page<Post> postPage, List<CommentInResponse> comments) {
        List<PostInResponse> posts = new ArrayList<>();
        for (Post item : postPage.getContent()) {
            addPostInResponseToPosts(comments, posts, item);
        }
        return posts;
    }

    private HashSet<Integer> getBlockedUsers(List<BlocksBetweenUsers> blocksList) {
        HashSet<Integer> blockedUsers = new HashSet<>();
        for (BlocksBetweenUsers block : blocksList) {
            blockedUsers.add(block.getDst().getId());
        }
        return blockedUsers;
    }

    private List<Post> cleanBlockedPosts(Page<Post> originPosts) {
        int currentUserId = ContextUtilities.getCurrentUserId();
        List<BlocksBetweenUsers> blocksBetweenUsers = blocksBetweenUsersRepository.findBySrc_Id(currentUserId);
        List<Post> postsList = originPosts.getContent();
        ArrayList<Post> cleanedPosts = new ArrayList<>();
        if (!(blocksBetweenUsers.isEmpty())) {
            HashSet<Integer> blockedUsers = getBlockedUsers(blocksBetweenUsers);
            int size = postsList.size();
            for (int i = 0; i < size; i++) {
                if (!blockedUsers.contains(postsList.get(i).getAuthor().getId())) {
                    cleanedPosts.add(postsList.get(i));
                }
            }
        } else {
            return postsList;
        }
        return cleanedPosts;
    }

    private List<PostInResponse> extractPostList(List<Post> postList, List<CommentInResponse> comments) {
        List<PostInResponse> posts = new ArrayList<>();
        for (Post item : postList) {
            addPostInResponseToPosts(comments, posts, item);
        }
        return posts;
    }

    private void addPostInResponseToPosts(List<CommentInResponse> comments, List<PostInResponse> posts, Post item) {
        PostInResponse postInResponse = new PostInResponse(item, comments, 0);
        if (item.getTime().isBefore(Instant.now())) {
            postInResponse.setType(PostType.POSTED);
        } else {
            postInResponse.setType(PostType.QUEUED);
        }
        if (!(postInResponse.getType() == PostType.QUEUED && postInResponse.getAuthor().getId()
                != personService.getAuthUser().getId())) {
            posts.add(postInResponse);
        }
    }

}
