package main.repository;

import main.model.Country;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CountryRepository extends PagingAndSortingRepository<Country, Integer> {

    Country findById(int countryId);

    Page<Country> findAll(Pageable pageable);

    Page<Country> findByTitleIgnoreCaseContaining(String title, Pageable pageable);

    Optional<Country> findByTitle(String title);
}
