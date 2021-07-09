package main.repository;

import main.model.PostTag;
import org.springframework.data.repository.CrudRepository;

public interface PostTagRepository extends CrudRepository<PostTag, Integer> {
}
