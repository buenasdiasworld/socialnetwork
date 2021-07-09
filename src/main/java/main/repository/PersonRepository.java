package main.repository;

import main.model.Person;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface PersonRepository extends JpaRepository<Person, Integer> {

    Optional<Person> findByEmail(String email);

    Person findById(int id);

    Optional<Person> findByConfirmationCode(String confirmationCode);


    List<Person> findAll();

    List<Optional<Person>> findByCityId(Integer cityId);

    List<Optional<Person>> findByLastNameLikeOrFirstNameLike(String lastName, String firstName);

    @Query(nativeQuery = true, value = "SELECT * FROM person "
        + "WHERE (is_deleted != 1) and (:firstName is null or first_name = :firstName) and (:lastName is null or last_name = :lastName) and "
        + "(:ageFrom is null or birth_date <= :ageFrom) and (:ageTo is null or birth_date >= :ageTo) and "
        + "(:countryId is null or country_id = :countryId) and "
        + "(COALESCE(:cityIds) is null or (city_id IN (:cityIds))) order by person.id",

        countQuery = "SELECT * FROM person "
            + "WHERE (country_id <> 239) and(:firstName is null or first_name = :firstName) and (:lastName is null or last_name = :lastName) and "
            + "(:ageFrom is null or birth_date <= :ageFrom) and (:ageTo is null or birth_date >= :ageTo) and "
            + "(:countryId is null or country_id = :countryId) and "
            + "(COALESCE(:cityIds) is null or (city_id IN (:cityIds)))")

    Page<Person> findPersonByNameLastNameAgeCityCountry(
        @Param("firstName") String firstName,
        @Param("lastName") String lastName,
        @Param("ageFrom") Date ageMore,
        @Param("ageTo") Date ageLess,
        @Param("countryId") Integer countryId,
        @Param("cityIds") Set<Integer> cityIds,
        Pageable pagable
    );

  Optional<Person> findByPhone(String phone);

  Optional<Person> findByTelegramId(long chatId);
}
