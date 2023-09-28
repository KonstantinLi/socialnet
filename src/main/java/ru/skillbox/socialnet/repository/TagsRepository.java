package ru.skillbox.socialnet.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.skillbox.socialnet.entity.postrelated.Tag;

import java.util.Optional;

@Repository
public interface TagsRepository extends CrudRepository<Tag, Long> {
    Optional<Tag> findByTag(String tag);
}
