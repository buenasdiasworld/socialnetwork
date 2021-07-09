package main.repository;

import main.model.Message;
import main.model.ReadStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MessageRepository extends JpaRepository<Message, Integer> {
    Page<Message> findByDialog_idAndRecipient_id(int dialogId, int recipientId, Pageable pageable);
    long countByReadStatusAndRecipient_idAndDialog_id(ReadStatus readStatus, int recipientId, int dialogId);
    Message findById(int messageId);
    long countByReadStatusAndRecipient_id(ReadStatus readStatus, int recipientId);
    Message findTopByDialog_idOrderByTimeDesc(int dialogId);
}

