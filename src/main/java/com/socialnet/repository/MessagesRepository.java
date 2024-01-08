package com.socialnet.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.socialnet.entity.dialogrelated.Message;

@Repository
public interface MessagesRepository extends JpaRepository<Message, Long> {
    long countByIsDeleted(boolean isDeleted);

    long countByDialogIdAndIsDeleted(
            long dialogId, boolean isDeleted
    );

    long countByAuthorIdAndRecipientId(Long authorId, Long recipientId);
}
