package ru.skillbox.socialnet.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.skillbox.socialnet.entity.Friendship;

import java.util.Optional;

@Repository
public interface FriendshipsRepository extends CrudRepository<Friendship, Long> {
    Optional<Friendship> findBySrcPersonIdAndDstPersonId(Long srcPersonId, Long dstPersonId);
}
