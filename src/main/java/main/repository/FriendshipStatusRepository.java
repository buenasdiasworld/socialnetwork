package main.repository;

import main.model.FriendshipStatus;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FriendshipStatusRepository extends CrudRepository<FriendshipStatus, Integer> {
    FriendshipStatus findById(int statusId);
}
