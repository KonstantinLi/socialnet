package ru.skillbox.socialnet.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;
import ru.skillbox.socialnet.entity.dialogrelated.Dialog;
import ru.skillbox.socialnet.entity.personrelated.Person;

import java.util.List;
import java.util.Optional;

public interface DialogRepository extends JpaRepository<Dialog, Long> {
    @Modifying
    @Transactional
    @Query("delete Dialog d where d.firstPerson.id = ?1 and d.secondPerson.id = ?1")
    void deleteByFirstPerson_IdAndSecondPerson_Id(Long id);

    List<Dialog> findByFirstPerson_IdOrSecondPerson_Id(Long id, Long id1);

    @Query(" select d from Dialog d where d.firstPerson.id = :userId or d.secondPerson.id = :userId")
    List<Dialog> getDialogsByUserId(Long userId);

    @Query("""
              select d from Dialog d
               where (d.firstPerson.id = :firstPersonId and d.secondPerson.id = :secondPersonId)
                  or (d.firstPerson.id = :secondPersonId and d.secondPerson.id = :firstPersonId)
            """)
    Optional<Dialog> findDialogByPersonIds(Long firstPersonId, Long secondPersonId);
}
