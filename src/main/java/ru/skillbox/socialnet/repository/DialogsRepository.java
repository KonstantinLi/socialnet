package ru.skillbox.socialnet.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.skillbox.socialnet.entity.dialogrelated.Dialog;

@Repository
public interface DialogsRepository extends JpaRepository<Dialog, Long> {
    long countByFirstPersonIdOrSecondPersonId(Long firstPersonId, Long secondPersonId);
}
