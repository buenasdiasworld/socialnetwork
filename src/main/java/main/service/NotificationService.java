package main.service;

import main.data.request.NotificationSettingsRequest;
import main.data.response.NotificationSettingsResponse;
import main.data.response.base.ListResponse;
import main.data.response.base.Response;
import main.data.response.type.InfoInResponse;
import main.data.response.type.NotificationResponse;
import main.model.*;

import java.util.Set;

public interface NotificationService {

    ListResponse<NotificationResponse> list(int offset, int itemPerPage, boolean needToRead);

    ListResponse<NotificationResponse> list(int offset, int itemPerPage, boolean needToRead, Long telegramId);

    ListResponse<NotificationResponse> read(int id, boolean all);

    Response<InfoInResponse> set(NotificationSettingsRequest request);

    Response<Set<NotificationSettingsResponse>> getSettings() ;

    void setNotification(PostComment postComment);

    void setNotification(Friendship friendship);

    void setNotification(Post post);

    void setNotification(Message message);

    void setNotification(Like like);

    void deleteNotification(Friendship friendship1, Friendship friendship2);

    void deleteNotification(Post post);

    void deleteNotification(PostComment comment);

    void deleteNotification(Like like);
}
