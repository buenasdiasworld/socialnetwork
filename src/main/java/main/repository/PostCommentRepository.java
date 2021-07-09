package main.repository;

import main.model.PostComment;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

@Repository
public interface PostCommentRepository extends CrudRepository<PostComment, Integer> {
    @Query(value = "SELECT * FROM post_comment WHERE post_id IN (:list)",
            nativeQuery = true)
    List<PostComment> getCommentsByList(Set<Integer> list);

    @Query(value = "SELECT * FROM post_comment WHERE post_id = (:postId)",
            nativeQuery = true)
    List<PostComment> findAllByPostId(Integer postId);

    @Query(value = "SELECT DISTINCT * FROM post_comment WHERE `parent_id` = (:commentId)",
            nativeQuery = true)
    List<PostComment> subCommentsG(Integer commentId);

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM post_comment where post_id = ?1",
            nativeQuery = true)
    void deleteAllByPostId(Integer postId);
}
