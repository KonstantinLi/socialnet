package ru.skillbox.socialnet.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.skillbox.socialnet.entity.postrelated.Post;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PostsRepository extends CrudRepository<Post, Long> {
    Optional<Post> findByIdAndIsDeleted(long Id, boolean isDeleted);

    long countByAuthorIdAndIsDeleted(
            long authorId, boolean isDeleted
    );
    List<Post> findAllByAuthorIdAndIsDeleted(
            long authorId, boolean isDeleted, Pageable pageable
    );

    long countByIsDeletedAndTimeGreaterThan(
            boolean isDeleted, LocalDateTime time
    );
    List<Post> findAllByIsDeletedAndTimeGreaterThan(
            boolean isDeleted, LocalDateTime time, Pageable pageable
    );

    long countByIsDeleted(boolean isDeleted);
}
