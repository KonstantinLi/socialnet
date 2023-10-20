package ru.skillbox.socialnet.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.skillbox.socialnet.entity.dialogrelated.Dialog;

public interface DialogRepository extends JpaRepository<Dialog, Long> {

  @Query(" select d from Dialog d where d.firstPerson.id = :userId or d.secondPerson.id = :userId")
  List<Dialog> getDialogsByUserId(Long userId);

  @Query("""
          select d from Dialog d
           where (d.firstPerson.id = :firstPersonId and d.secondPerson.id = :secondPersonId)
              or (d.firstPerson.id = :secondPersonId and d.secondPerson.id = :firstPersonId)
        """)
  Optional<Dialog> findDialogByPersonIds(Long firstPersonId, Long secondPersonId);
}
