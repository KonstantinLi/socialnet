package ru.skillbox.socialnet.repository;

import org.springframework.stereotype.Repository;
import org.springframework.data.repository.CrudRepository;

import ru.skillbox.socialnet.entity.postrelated.Like;
import ru.skillbox.socialnet.entity.enums.LikeType;

import java.util.Optional;
import java.util.Set;

@Repository
public interface LikesRepository extends CrudRepository<Like, Long> {
    Set<Like> findAllByTypeAndEntityId(LikeType type, long entityId);
    Optional<Like> findByPersonIdAndTypeAndEntityId(long personId, LikeType type, long entityId);
    void deleteByPersonIdAndTypeAndEntityId(long personId, LikeType type, long entityId);
    int countByTypeAndEntityId(LikeType type, long entityId);
    boolean existsByPersonId(long personId);
}
