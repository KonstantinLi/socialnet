package ru.skillbox.socialnet.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.skillbox.socialnet.entity.post.PostComment;

import java.util.List;

@Repository
public interface PostCommentsRepository extends CrudRepository<PostComment, Long> {
    List<PostComment> findAllByAuthorId(Long authorId);
}
