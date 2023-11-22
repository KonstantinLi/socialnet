package ru.skillbox.socialnet.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.skillbox.socialnet.entity.postrelated.PostComment;

import java.util.List;
import java.util.Optional;

@Repository
public interface PostCommentsRepository extends CrudRepository<PostComment, Long> {
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

    @Query(nativeQuery = true, value = """
            SELECT COUNT(c)
            FROM post_comments c
            WHERE c.post_id = :postId
            and c.parent_id is null
            and (not c.is_blocked or c.author_id = :userId)
            """)
    long countRootCommentsByPostId(
            @Param("postId") long postId,
            @Param("userId") long userId
    );
    @Query(nativeQuery = true, value = """
            SELECT *
            FROM post_comments c
            WHERE c.post_id = :postId
            and c.parent_id is null
            and (not c.is_blocked or c.author_id = :userId)
            """)
    List<PostComment> findAllRootCommentsByPostId(
            @Param("postId") long postId,
            @Param("userId") long userId,
            Pageable pageable
    );
}
