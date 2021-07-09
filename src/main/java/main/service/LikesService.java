package main.service;

import main.data.request.LikeRequest;
import main.data.response.base.Response;
import main.data.response.type.LikesWithUsers;
import main.model.Like;

public interface LikesService {
    Response<LikesWithUsers> isLiked(Integer userId, int itemId, String type);
    Response<LikesWithUsers> getLikes(int itemId, String type);
    Response<LikesWithUsers> setLike(LikeRequest request);
    Response<LikesWithUsers> deleteLike(int itemId, String type);
    Like findById(int id);
}
