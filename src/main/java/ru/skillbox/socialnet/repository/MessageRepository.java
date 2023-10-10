package ru.skillbox.socialnet.repository;

import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.skillbox.socialnet.entity.dialogrelated.Message;

public interface MessageRepository extends JpaRepository<Message, Long> {

  Optional<Message> getFirstByDialog_IdOrderByTimeDesc(Long dialogId);

  Page<Message> getMessagesByDialog_Id(Long dialogId, Pageable pageable);

  @Query(" select count(m) from Message m where m.recipient.id = :userId and m.readStatus = 'UNREAD' " )
  long countUnreadMessagesByUserId(Long userId);

  @Override
  Message getById(Long aLong);
}
