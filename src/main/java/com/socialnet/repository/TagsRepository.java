package com.socialnet.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import com.socialnet.entity.postrelated.Tag;

import java.util.Optional;

@Repository
public interface TagsRepository extends CrudRepository<Tag, Long> {
    Optional<Tag> findByTagName(String tagName);
}
