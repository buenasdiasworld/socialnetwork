package main.repository;

import main.model.Like;
import main.model.LikeType;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface LikesRepository extends CrudRepository<Like, Integer> {
    Optional<Like> findByItemIdAndType(int itemId, LikeType type);
    Optional<Like> findByItemIdAndPersonIdAndType(int itemId, int personId, LikeType type);

    List<Like> findAllByItemIdAndType(int itemId, LikeType type);

    void deleteByItemIdAndPersonId(int itemId, int userId);
    void deleteByItemIdAndPersonIdAndType(int itemId, int personId, LikeType type);
}
