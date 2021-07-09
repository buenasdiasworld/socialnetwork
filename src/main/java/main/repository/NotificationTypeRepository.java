package main.repository;

import main.model.NotificationType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface NotificationTypeRepository extends JpaRepository<NotificationType, Integer> {

    @Query(value = "SELECT * " +
            "FROM notification_type " +
            "WHERE code = ?1", nativeQuery = true)
    Optional<NotificationType> findByCode(String code);
}
