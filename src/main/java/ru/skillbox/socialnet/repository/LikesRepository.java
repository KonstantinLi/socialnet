package ru.skillbox.socialnet.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.skillbox.socialnet.entity.enums.LikeType;
import ru.skillbox.socialnet.entity.postrelated.Like;

import java.util.Optional;
import java.util.Set;

@Repository
public interface LikesRepository extends CrudRepository<Like, Long> {
    Set<Like> findAllByTypeAndEntityId(LikeType type, long entityId);

    Optional<Like> findByPersonIdAndTypeAndEntityId(long personId, LikeType type, long entityId);

    void deleteByPersonIdAndTypeAndEntityId(long personId, LikeType type, long entityId);

    long countByTypeAndEntityId(LikeType type, long entityId);

    boolean existsByTypeAndEntityIdAndPersonId(LikeType type, long entityId, long personId);

    boolean existsByPersonId(long personId);
}
