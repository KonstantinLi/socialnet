package ru.skillbox.socialnet.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.skillbox.socialnet.entity.postrelated.PostComment;

import java.util.List;
import java.util.Optional;

@Repository
public interface PostCommentsRepository extends CrudRepository<PostComment, Long> {
    Optional<PostComment> findByIdAndIsDeleted(long Id, boolean isDeleted);

    Optional<PostComment> findByIdAndPostIdAndIsDeleted(long Id, long postId, boolean isDeleted);

    long countByPostIdAndIsDeleted(
            long postId, boolean isDeleted
    );

    List<PostComment> findAllByPostIdAndIsDeleted(
            long postId, boolean isDeleted, Pageable pageable
    );

    long countByPostIdAndIsDeletedAndParentId(
            long postId, boolean isDeleted, Long parentId
    );
    List<PostComment> findAllByPostIdAndIsDeletedAndParentId(
            long postId, boolean isDeleted, Long parentId, Pageable pageable
    );

    long countByPostIdAndParentId(
            long postId, Long parentId
    );
    List<PostComment> findAllByPostIdAndParentId(
            long postId, Long parentId, Pageable pageable
    );
}
