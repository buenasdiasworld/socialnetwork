package main.repository;

import main.model.Friendship;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FriendsRepository extends CrudRepository<Friendship, Integer> {
    Page<Friendship> findByDst_IdAndStatusId(int dstId, Pageable pageable, int statusId);
    Page<Friendship> findBySrc_IdAndStatusId(int srcId, Pageable pageable, int statusId);
    Friendship findByDst_IdAndSrc_IdAndStatusId(int dstId, int srcId, int statusId);
    Friendship findBySrc_idAndDst_IdAndStatusId(int dstId, int scrId, int statusId);
    List<Friendship> findByDst_IdAndStatusId(int dstId, int statusId);
}
