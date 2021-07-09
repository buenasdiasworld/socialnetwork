package main.repository;

import main.model.Language;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LanguageRepository extends CrudRepository<Language, Integer> {
    Page<Language> findAll(Pageable pageable);
    Page<Language> findByTitleIgnoreCaseContaining(String title, Pageable pageable);
}

