package ru.skillbox.socialnet.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.skillbox.socialnet.entity.post.Post;

@Repository
public interface PostsRepository extends CrudRepository<Post, Long> {
}
