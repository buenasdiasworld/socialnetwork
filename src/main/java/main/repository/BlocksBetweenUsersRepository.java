package main.repository;

import main.model.BlocksBetweenUsers;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BlocksBetweenUsersRepository extends CrudRepository<BlocksBetweenUsers, Integer> {
    BlocksBetweenUsers findBySrc_IdAndDst_Id(int scrId, int dstId);
    List<BlocksBetweenUsers> findBySrc_Id(int scrId);
}
