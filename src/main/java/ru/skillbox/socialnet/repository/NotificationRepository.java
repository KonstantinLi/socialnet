package ru.skillbox.socialnet.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.skillbox.socialnet.entity.other.Notification;

import java.util.List;

@Repository
public interface NotificationRepository extends CrudRepository<Notification, Long> {
    @Query(value = "SELECT * FROM notifications WHERE person_id = ?1", nativeQuery = true)
    List<Notification> findAllByPersonId(Long personId);
}
