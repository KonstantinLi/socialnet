package ru.skillbox.socialnet.data.dao;

import org.springframework.stereotype.Repository;
import org.springframework.data.repository.CrudRepository;

import ru.skillbox.socialnet.data.entity.Like;

@Repository
public interface LikesRepository extends CrudRepository<Like, Long> {
}
