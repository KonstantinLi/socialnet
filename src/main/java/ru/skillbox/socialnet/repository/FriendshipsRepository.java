package ru.skillbox.socialnet.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.skillbox.socialnet.entity.FriendShip;

import java.util.Optional;

@Repository
public interface FriendshipsRepository extends CrudRepository<FriendShip, Long> {
    Optional<FriendShip> findBySrcPersonIdAndDstPersonId(Long srcPersonId, Long dstPersonId);
}
