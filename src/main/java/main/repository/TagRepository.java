package main.repository;

import java.util.List;
import main.model.Tag;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.Optional;
import org.springframework.data.repository.query.Param;

public interface TagRepository extends PagingAndSortingRepository<Tag, Integer> {
    Optional<Tag> findTagByTag(String name);
    Iterable<Tag> findByTagLike(String tag);

    @Query(nativeQuery = true, value = "SELECT * FROM tag WHERE tag.tag IN (:tagName)")
    List<Optional<Tag>> findTagsByTagNames( @Param("tagName") List <String> tags);
}
