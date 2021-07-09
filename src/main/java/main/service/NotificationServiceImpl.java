package main.service;

import lombok.RequiredArgsConstructor;
import main.core.ContextUtilities;
import main.core.OffsetPageRequest;
import main.data.request.NotificationSettingsRequest;
import main.data.response.NotificationSettingsResponse;
import main.data.response.base.ListResponse;
import main.data.response.base.Response;
import main.data.response.type.InfoInResponse;
import main.data.response.type.NotificationResponse;
import main.exception.BadRequestException;
import main.exception.apierror.ApiError;
import main.model.*;
import main.repository.NotificationRepository;
import main.repository.NotificationTypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.Period;
import java.time.Year;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final NotificationTypeRepository notificationTypeRepository;
    private final PersonService personService;
    private CommentService commentService;
    private PostService postService;
    private FriendsService friendsService;
    private DialogService dialogService;
    private LikesService likesService;

    @Autowired
    public void setPostService(@Lazy PostService postService) {
        this.postService = postService;
    }

    @Autowired
    public void setCommentService(@Lazy CommentService commentService) {
        this.commentService = commentService;
    }

    @Autowired
    public void setFriendsService(@Lazy FriendsService friendsService) {
        this.friendsService = friendsService;
    }

    @Autowired
    public void setDialogService(@Lazy DialogService dialogService) {
        this.dialogService = dialogService;
    }

    @Autowired
    public void setLikesService(@Lazy LikesService likesService) {
        this.likesService = likesService;
    }

    @Value("${notifications.storagePeriod}")
    public String notificationsStoragePeriod;

    private static final String INVALID_REQUEST = "invalid_request";

    @Override
    public ListResponse<NotificationResponse> list(int offset, int itemPerPage, boolean needToRead) {
        return list(offset, itemPerPage, needToRead, null);
    }

    @Override
    public ListResponse<NotificationResponse> list(int offset, int itemPerPage, boolean needToRead, Long telegramId) {
        Person person;
        if (telegramId == null) {
            person = ContextUtilities.getCurrentPerson();
        } else {
            person = personService.loginTelegram(telegramId);
        }

        // получаем для текущего пользователя list с перечнем id типов уведомлений, которые он акцептовал
        List<Integer> types = person.getNotificationSettings().entrySet().stream()
                .filter(Map.Entry::getValue).map(Map.Entry::getKey).collect(Collectors.toList());

        List<NotificationResponse> notificationResponses = new ArrayList<>();

        Pageable pageable;
        Page<Notification> notifications;

        if (itemPerPage > 0) {
            pageable = new OffsetPageRequest(offset, itemPerPage, Sort.unsorted());
        } else {
            pageable = Pageable.unpaged();
        }

        try {
            // делаем выборку только акцептованных текущим пользователем уведомлений
            int days = Integer.parseInt(notificationsStoragePeriod.trim());
            notifications = notificationRepository
                    .findByReceiverAndTypeAndTimeLaterThanEqual(person.getId(), types,
                            Instant.now().minus(Period.ofDays(days)), pageable);
        } catch (BadRequestException ex) {
            throw new BadRequestException(new ApiError(INVALID_REQUEST, "Bad request"));
        }

        if (needToRead) {
            notifications.forEach(notification ->
                    notification.setReadStatus(NotificationReadStatusCode.READ)
            );
            notificationRepository.saveAll(notifications);
        }

        notifications.forEach(i -> notificationResponses.add(convertToDto(i)));

        return new ListResponse<>(
                notificationResponses,
                notifications.getTotalElements(),
                offset,
                itemPerPage
        );
    }

    @Override
    public ListResponse<NotificationResponse> read(int id, boolean all) {
        if (all) {
            return list(0, 20, true);
        } else {
            Notification notification = notificationRepository.findById(id).orElseThrow(
                    () -> new BadRequestException(new ApiError(INVALID_REQUEST,
                            "post is not found")));
            List<NotificationResponse> notificationResponses = new ArrayList<>();
            NotificationResponse notificationResponse = convertToDto(notification);
            notificationResponses.add(notificationResponse);
            return new ListResponse<>(notificationResponses, 1, 0, 20);
        }
    }

    private NotificationResponse convertToDto(Notification notification) {
        String info;
        Person author;
        switch (notification.getType().getCode()) {
            case POST_COMMENT:
            case COMMENT_COMMENT:
                PostComment postComment = commentService.getComment(notification.getEntityId());
                info = postComment.getCommentText();
                author = postComment.getAuthor();
                break;
            case MESSAGE:
                Message message = dialogService.findById(notification.getEntityId());
                info = message.getMessageText();
                author = message.getAuthor();
                break;
            case POST:
                Post post = postService.findById(notification.getEntityId());
                info = post.getTitle();
                author = post.getAuthor();
                break;
            case FRIEND_REQUEST:
                Friendship friendship = friendsService.findById(notification.getEntityId());
                info = notification.getContact();
                author = friendship.getSrc();
                break;
            case FRIEND_BIRTHDAY:
                friendship = friendsService.findById(notification.getEntityId());
                Person personSrs = friendship.getSrc();
                Person personDst = friendship.getDst();
                info = notification.getContact();
                author = notification.getReceiver().equals(personSrs) ? personDst : personSrs;
                break;
            case LIKE:
                Like like = likesService.findById(notification.getEntityId());
                info = notification.getContact();
                author = like.getPerson();
                break;
            default:
                info = "";
                author = null;
        }
        return new NotificationResponse(notification, info, author);
    }

    @Override
    public Response<InfoInResponse> set(NotificationSettingsRequest request) {

        Person receiver = ContextUtilities.getCurrentPerson();

        boolean isEnabled = request.getEnable();
        int notificationTypeId = getIdByCode(request.getNotificationType());

        Map<Integer, Boolean> settings = receiver.getNotificationSettings();
        settings.putIfAbsent(notificationTypeId, isEnabled);
        settings.replace(notificationTypeId, isEnabled);

        personService.save(receiver);

        InfoInResponse info = new InfoInResponse("Успешная смена статуса");
        Response<InfoInResponse> response = new Response<>();
        response.setData(info);
        return response;
    }

    @Override
    public Response<Set<NotificationSettingsResponse>> getSettings() {

        Person receiver = ContextUtilities.getCurrentPerson();
        Map<Integer, Boolean> settings = receiver.getNotificationSettings();

        Set<NotificationSettingsResponse> res = settings.entrySet().stream().map(
                s -> {
                    int id = s.getKey();
                    return new NotificationSettingsResponse(
                            getNotificationTypeById(id).getCode().toString(),
                            s.getValue());
                }).collect(Collectors.toSet());
        return new Response<>(res);
    }

    @Override
    public void setNotification(PostComment postComment) {
        Notification notification = new Notification();
        if (postComment.getParent() != null) {
            //комментарий на комментарий
            Person parentCommentAuthor = postComment.getParent().getAuthor();
            if (postComment.getAuthor().getId() != parentCommentAuthor.getId()) {
                notification.setReceiver(postComment.getParent().getAuthor());
                notification.setEntityId(postComment.getId());
                notification.setType(getNotificationType(NotificationTypeCode.COMMENT_COMMENT));
                notificationRepository.save(notification);
            }
        } else {
            //комментарий на пост
            Person postAuthor = postComment.getPost().getAuthor();
            if (postComment.getAuthor().getId() != postAuthor.getId()) {
                notification.setReceiver(postComment.getPost().getAuthor());
                notification.setEntityId(postComment.getId());
                notification.setType(getNotificationType(NotificationTypeCode.POST_COMMENT));
                notificationRepository.save(notification);
            }
        }
    }

    @Override
    public void setNotification(Friendship friendship) {
        // отправка уведомления о направлении или принятии запроса в друзья
        Notification not1 = new Notification();
        not1.setReceiver(friendship.getDst());
        not1.setEntityId(friendship.getId());
        not1.setType(getNotificationType(NotificationTypeCode.FRIEND_REQUEST));
        not1.setContact(friendship
                .getStatus().equals(friendsService.findFriendshipStatusById(1)) ?
                "Запрос направлен" :
                "Запрос принят");
        notificationRepository.save(not1);

        //если запрос принят, отправка обоим друзьям уведомления о дне рождения
        SimpleDateFormat formatForDate = new SimpleDateFormat("EEEE, dd MMMM");
        Calendar cal = Calendar.getInstance();
        if (friendship.getStatus().equals(friendsService.findFriendshipStatusById(2))) {
            // создаем уведомление для первого друга
            Notification not2 = new Notification();
            not2.setReceiver(friendship.getDst());
            not2.setEntityId(friendship.getId());
            not2.setType(getNotificationType(NotificationTypeCode.FRIEND_BIRTHDAY));
            // устанавливаем дату уведомления за сутки до д/р
            cal.setTime(friendship.getSrc().getBirthDate());
            cal.add(Calendar.DATE, -1);
            cal.set(Calendar.YEAR, Year.now().getValue());
            not2.setSentTime(Instant.ofEpochMilli(cal.getTime().getTime()));
            not2.setContact("У " + friendship.getSrc().getFirstName() + " " +
                    friendship.getSrc().getLastName() + " в " +
                    formatForDate.format(friendship.getSrc().getBirthDate()) +
                    ", день рождения");

            // создаем уведомление для второго друга
            Notification not3 = new Notification();
            not3.setReceiver(friendship.getSrc());
            not3.setEntityId(friendship.getId());
            not3.setType(getNotificationType(NotificationTypeCode.FRIEND_BIRTHDAY));
            // устанавливаем дату уведомления за сутки до д/р
            cal.setTime(friendship.getDst().getBirthDate());
            cal.add(Calendar.DATE, -1);
            cal.set(Calendar.YEAR, Year.now().getValue());
            not3.setSentTime(Instant.ofEpochMilli(cal.getTime().getTime()));
            not3.setContact("У " + friendship.getDst().getFirstName() + " " +
                    friendship.getDst().getLastName() + " в " +
                    formatForDate.format(friendship.getSrc().getBirthDate()) +
                    ", день рождения");

            notificationRepository.save(not2);
            notificationRepository.save(not3);
        }
    }

    @Override
    public void setNotification(Post post) {
        List<Notification> notifications = new ArrayList<>();
        post.getAuthor().getRequestFr()
                .forEach(friendship -> {
                    Notification notification = new Notification();
                    notification.setReceiver(friendship.getSrc());
                    notification.setEntityId(post.getId());
                    notification.setType(getNotificationType(NotificationTypeCode.POST));
                    notifications.add(notification);
                });
        notificationRepository.saveAll(notifications);
    }

    @Override
    public void setNotification(Message message) {
        Notification notification = new Notification();
        notification.setReceiver(message.getRecipient());
        notification.setEntityId(message.getId());
        notification.setType(getNotificationType(NotificationTypeCode.MESSAGE));
        notification.setSentTime(message.getTime());
        notificationRepository.save(notification);
    }

    @Override
    public void setNotification(Like like) {
        Notification notification = new Notification();
        notification.setEntityId(like.getId());
        notification.setType(getNotificationType(NotificationTypeCode.LIKE));
        notification.setSentTime(like.getTime());
        if (like.getType().equals(LikeType.POST)) {
            Post post = postService.getPost(like.getItemId());
            if (post.getAuthor().getId() != like.getPerson().getId()) {
                notification.setReceiver(post.getAuthor());
                notification.setContact("на ваш пост: ".concat(post.getTitle()));
                notificationRepository.save(notification);
            }
        } else {
            PostComment comment = commentService.getComment(like.getItemId());
            if (comment.getAuthor().getId() != like.getPerson().getId()) {
                notification.setReceiver(comment.getAuthor());
                notification.setContact("на ваш комментарий: ".concat(comment.getCommentText()));
                notificationRepository.save(notification);
            }
        }
    }

    @Override
    public void deleteNotification(Friendship friendship1, Friendship friendship2) {
        // удаляем уведомления о запросах в друзья и о днях рождения
        List<Integer> typeId = new ArrayList<>();
        typeId.add(getIdByCode(NotificationTypeCode.FRIEND_REQUEST.toString()));
        typeId.add(getIdByCode(NotificationTypeCode.FRIEND_BIRTHDAY.toString()));
        List<Integer> entityId = new ArrayList<>();
        entityId.add(friendship1.getId());
        entityId.add(friendship2.getId());
        notificationRepository.deleteByTypeIdAndEntityId(typeId, entityId);
        // удаляем посты бывших друзей
        typeId.clear();
        typeId.add(getIdByCode(NotificationTypeCode.POST.toString()));
        entityId.clear();
        postService.findByAuthor(friendship1.getDst()).stream()
                .map(Post::getId).collect(Collectors.toCollection(() -> entityId));
        postService.findByAuthor(friendship1.getSrc()).stream()
                .map(Post::getId).collect(Collectors.toCollection(() -> entityId));
        notificationRepository.deleteByTypeIdAndEntityId(typeId, entityId);
    }

    @Override
    public void deleteNotification(Post post) {
        // удаляем нотификации к посту
        List<Integer> typeId = new ArrayList<>();
        typeId.add(getIdByCode(NotificationTypeCode.POST.toString()));
        List<Integer> postsId = new ArrayList<>();
        postsId.add(post.getId());
        notificationRepository.deleteByTypeIdAndEntityId(typeId, postsId);
        //удаляем нотификации к комментариям
        typeId.clear();
        typeId.add(getIdByCode(NotificationTypeCode.POST_COMMENT.toString()));
        typeId.add(getIdByCode(NotificationTypeCode.COMMENT_COMMENT.toString()));
        List<Integer> commentsId = new ArrayList<>();
        List<PostComment> comments = commentService.findAllByPostId(post.getId());
        comments.stream().map(PostComment::getId).collect(Collectors.toCollection(() -> commentsId));
        notificationRepository.deleteByTypeIdAndEntityId(typeId, commentsId);
        //удаляем нотификации к лайкам к посту и к комментариям к посту
        typeId.clear();
        typeId.add(getIdByCode(NotificationTypeCode.LIKE.toString()));
        List<Integer> likesId = new ArrayList<>();
        comments.forEach(comment -> comment.getLikes().stream()
                .map(Like::getId).collect(Collectors.toCollection(() -> likesId)));
        post.getLikes().stream()
                .map(Like::getId).collect(Collectors.toCollection(() -> likesId));
        notificationRepository.deleteByTypeIdAndEntityId(typeId, likesId);
    }

    @Override
    public void deleteNotification(PostComment comment) {
        List<Integer> typeId = new ArrayList<>();
        List<Integer> entityId = new ArrayList<>();
        //удаляем нотификации к комментариям, включая подчиненные по иерархии
        typeId.add(getIdByCode(NotificationTypeCode.POST_COMMENT.toString()));
        typeId.add(getIdByCode(NotificationTypeCode.COMMENT_COMMENT.toString()));
        entityId.add(comment.getId());
        List<PostComment> comments = commentService.subComments(comment);
        comments.add(comment);
        comments.stream().map(PostComment::getId)
                .collect(Collectors.toCollection(() -> entityId));
        notificationRepository.deleteByTypeIdAndEntityId(typeId, entityId);
        //удаляем нотификации к лайкам к комментариям, включая подчиненные по иерархии
        typeId.clear();
        typeId.add(getIdByCode(NotificationTypeCode.LIKE.toString()));
        List<Integer> likesId = new ArrayList<>();
        comments.forEach(comm -> comm.getLikes().stream()
                .map(Like::getId).collect(Collectors.toCollection(() -> likesId)));
        notificationRepository.deleteByTypeIdAndEntityId(typeId, likesId);
    }

    @Override
    public void deleteNotification(Like like) {
        List<Integer> typeId = new ArrayList<>();
        typeId.add(getIdByCode(NotificationTypeCode.LIKE.toString()));
        List<Integer> entityId = new ArrayList<>();
        entityId.add(like.getId());
        notificationRepository.deleteByTypeIdAndEntityId(typeId, entityId);
    }

    private NotificationType getNotificationType(NotificationTypeCode notificationTypeCode) {
        return notificationTypeRepository.findByCode(notificationTypeCode.toString())
                .orElseThrow(EntityNotFoundException::new);
    }

    private int getIdByCode(String code) {
        return notificationTypeRepository.findByCode(code)
                .orElseThrow(EntityNotFoundException::new).getId();
    }

    private NotificationType getNotificationTypeById(int id) {
        return notificationTypeRepository.findById(id)
                .orElseThrow(EntityNotFoundException::new);
    }
}
