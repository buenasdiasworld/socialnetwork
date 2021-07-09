package main.service;

import main.data.response.FriendsResponse;
import main.model.Friendship;
import main.model.FriendshipStatus;

import java.util.List;

public interface FriendsService {

    FriendsResponse getFriends(String name, int offset, int limit);

    FriendsResponse addFriend(int id);

    FriendsResponse deleteFriend(int id);

    FriendsResponse blockFriend(int id);

    FriendsResponse getRecommendations(int offset, int limit);

    FriendsResponse getRequests(int offset, int limit);

    Friendship findById(int id);

    List<Friendship> findByDst_IdAndStatusId(int dstId, int statusId);

    FriendshipStatus findFriendshipStatusById(int id);
}
