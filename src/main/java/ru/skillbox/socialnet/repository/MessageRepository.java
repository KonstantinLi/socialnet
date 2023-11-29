package ru.skillbox.socialnet.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;
import ru.skillbox.socialnet.entity.dialogrelated.Message;
import ru.skillbox.socialnet.entity.personrelated.Person;

import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Long> {
    List<Message> findByAuthor_IdOrRecipient_Id(Long id, Long id1);
    @Query("select m from Message m where m.dialog.id = :dialogId and m.isDeleted = false order by m.time")
    Page<Message> getMessagesByDialogId(Long dialogId, Pageable pageable);

    @Query(" select count(m) from Message m where m.recipient.id = :userId and m.readStatus = 'UNREAD' ")
    long countUnreadMessagesByUserId(Long userId);

    @Query(" select count(m) from Message m where m.dialog.id = :dialogId and m.readStatus = 'UNREAD' ")
    long countUnreadMessagesByDialogId(Long dialogId);

    @Query(" select m from Message m where m.dialog.id = :dialogId and m.recipient.id = :userId and m.readStatus = 'UNREAD' ")
    List<Message> getUnreadMessagesByDialogIdAndUserId(Long dialogId, Long userId);

    @Modifying
    @Transactional
    @Query("delete Message m where m.author.id = ?1 and m.recipient.id = ?1")
    void deleteByAuthor_IdAndRecipient_Id(Long id);
}
