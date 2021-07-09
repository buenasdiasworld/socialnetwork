package main.repository;

import main.model.Dialog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface DialogRepository extends JpaRepository<Dialog, Integer> {
    Page<Dialog> findByPersons_id(int personId, Pageable pageable);

    @Query(
            value = "SELECT DISTINCT d.* " +
                    "FROM dialog d " +
                    "INNER JOIN dialog2person dp1 ON dp1.dialog_id = d.id " +
                    "INNER JOIN dialog2person dp2 ON dp2.dialog_id = d.id " +
                    "WHERE dp1.person_id = ?1 AND dp2.person_id = ?2 AND d.is_group = 0",
            nativeQuery = true
    )
    Dialog findTetATet(int firstPersonId, int secondPersonId);

    Dialog findById(int dialogId);
}
