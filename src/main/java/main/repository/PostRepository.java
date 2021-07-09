package main.repository;

import java.util.Date;
import java.util.List;
import java.util.Set;
import main.model.Person;
import main.model.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface PostRepository extends CrudRepository<Post, Integer> {

  Page<Post> findAll(Pageable pageable);

  Page<Post> findByTitle(String name, Pageable pageable);

  Page<Post> findByAuthor(Person person, Pageable pageable);

  List<Post> findByAuthor(Person person);

  @Modifying
  @Transactional
  @Query(nativeQuery = true, value = "delete from post where (author_id = :authorId and id > 0)")
  void deleteByAuthorId( @Param("authorId")Integer authorId);

  @Query(nativeQuery = true, value =
      "SELECT DISTINCT post.* FROM post "
          + "JOIN person ON person.id = post.author_id "
          + "JOIN post2tag ON post2tag.post_id = post.id "
          + "WHERE "
          + "((:text is null) OR (post_text LIKE :text) OR (title LIKE :text)) AND "
          + "((:dateFrom is null AND :dateTo is null) or (time >= :dateFrom AND time <= :dateTo)) AND  "
          + "(COALESCE(:authorId) is null or (author_id IN (:authorId))) AND "
          + "(COALESCE(:tagId) is null or (post2tag.tag_id IN (:tagId))) order by post.time DESC",
      countQuery = "SELECT DISTINCT post.* FROM post "
          + "JOIN person ON person.id = post.author_id "
          + "JOIN post2tag ON post2tag.post_id = post.id "
          + "WHERE "
          + "((:text is null) OR (post_text LIKE :text) OR (title LIKE :text)) AND "
          + "((:dateFrom is null AND :dateTo is null) or (time >= :dateFrom AND time <= :dateTo)) AND  "
          + "(COALESCE(:authorId) is null or (author_id IN (:authorId))) AND "
          + "(COALESCE(:tagId) is null or (post2tag.tag_id IN (:tagId)))"
  )
  Page<Post> findByTextPeriodAuthorTags(
      @Param("text") String text,
      @Param("dateFrom") Date dateFrom,
      @Param("dateTo") Date dateTo,
      @Param("authorId") Set<Integer> authorId,
      @Param("tagId") Set<Integer> tags,
      Pageable pagable
  );

  @Query(nativeQuery = true, value =
      "SELECT DISTINCT post.* FROM post "
          + "JOIN person ON person.id = post.author_id "
          + "WHERE "
          + "((:text is null) OR (post_text LIKE :text) OR (title LIKE :text)) AND "
          + "((:dateFrom is null AND :dateTo is null) or (time >= :dateFrom AND time <= :dateTo)) AND  "
          + "(COALESCE(:authorId) is null or (author_id IN (:authorId))) order by post.time DESC",  countQuery =
      "SELECT count(*) FROM post "
          + "JOIN person ON person.id = post.author_id "
          + "WHERE "
          + "((:text is null) OR (post_text LIKE :text) OR (title LIKE :text)) AND "
          + "((:dateFrom is null AND :dateTo is null) or (time >= :dateFrom AND time <= :dateTo)) AND  "
          + "(COALESCE(:authorId) is null or (author_id IN (:authorId))) "
  )
  Page<Post> findByTextPeriodAuthorNoTags(
      @Param("text") String text,
      @Param("dateFrom") Date dateFrom,
      @Param("dateTo") Date dateTo,
      @Param("authorId") Set<Integer> authorId,
      Pageable pagable
  );

}
