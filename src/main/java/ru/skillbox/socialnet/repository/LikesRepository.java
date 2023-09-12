package ru.skillbox.socialnet.repository;

import org.springframework.stereotype.Repository;
import org.springframework.data.repository.CrudRepository;

import ru.skillbox.socialnet.entity.Like;

@Repository
public interface LikesRepository extends CrudRepository<Like, Long> {
}
