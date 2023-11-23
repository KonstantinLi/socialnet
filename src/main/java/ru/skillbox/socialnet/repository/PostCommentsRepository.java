package ru.skillbox.socialnet.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.skillbox.socialnet.entity.postrelated.PostComment;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface PostCommentsRepository extends CrudRepository<PostComment, Long> {
    @Transactional
    @Modifying
    @Query("delete from PostComment p where p.post.id in ?1")
    void deleteByPost_IdIn(Collection<Long> postsIds);

    @Transactional
    @Modifying
    void deleteByAuthor_IdIn(Collection<Long> ids);

    Optional<PostComment> findByIdAndIsDeleted(long id, boolean isDeleted);

    Optional<PostComment> findByIdAndPostIdAndIsDeleted(long id, long postId, boolean isDeleted);

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
