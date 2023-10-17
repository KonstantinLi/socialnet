package ru.skillbox.socialnet.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.skillbox.socialnet.entity.dialogrelated.Message;

public interface MessageRepository extends JpaRepository<Message, Long> {

  @Query("select m from Message m where m.dialog.id = :dialogId and m.isDeleted = false order by m.time")
  Page<Message> getMessagesByDialogId(Long dialogId, Pageable pageable);

  @Query(" select count(m) from Message m where m.recipient.id = :userId and m.readStatus = 'UNREAD' " )
  long countUnreadMessagesByUserId(Long userId);

  @Query(" select count(m) from Message m where m.dialog.id = :dialogId and m.readStatus = 'UNREAD' " )
  long countUnreadMessagesByDialogId(Long dialogId);
}
