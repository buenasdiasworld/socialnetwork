package main.repository;

import java.util.List;
import java.util.Optional;
import main.model.City;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CityRepository extends PagingAndSortingRepository<City, Integer> {

  City findById(int cityId);

  Page<City> findByCountryId(int countryId, Pageable pageable);

  Page<City> findByCountryIdAndTitleIgnoreCaseContaining(int countryId, String title,
      Pageable pageable);

  List<Optional<City>> findByTitle(String title);
  List<Optional<City>> findByTitleAndCountryId(String title, Integer countryId);

  @Query(nativeQuery = true, value = "select * from city where country_id = :id limit 1")
  City findFirstCityFromCountry(int id);

}
