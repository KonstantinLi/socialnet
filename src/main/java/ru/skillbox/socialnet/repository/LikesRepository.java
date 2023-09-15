package ru.skillbox.socialnet.repository;

import org.springframework.stereotype.Repository;
import org.springframework.data.repository.CrudRepository;

import ru.skillbox.socialnet.entity.Like;
import ru.skillbox.socialnet.entity.enums.LikeType;

import java.util.Optional;

@Repository
public interface LikesRepository extends CrudRepository<Like, Long> {
    Optional<Like> findByPersonIdAndTypeAndEntityId(Long personId, LikeType type, Long entityId);
    int countByTypeAndEntityId(LikeType type, Long entityId);
    boolean existsByPersonId(Long personId);
}
