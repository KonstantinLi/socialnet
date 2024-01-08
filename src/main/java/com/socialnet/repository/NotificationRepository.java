package com.socialnet.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import com.socialnet.entity.other.Notification;

import java.util.List;

@Repository
public interface NotificationRepository extends CrudRepository<Notification, Long> {
    List<Notification> findAllByPerson_IdAndIsRead(Long personId, Boolean isRead);
}
