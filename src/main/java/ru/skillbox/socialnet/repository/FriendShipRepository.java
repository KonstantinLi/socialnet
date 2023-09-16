package ru.skillbox.socialnet.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.skillbox.socialnet.data.entity.FriendShip;

@Repository
public interface FriendShipRepository extends CrudRepository<FriendShip, Long> {
}
