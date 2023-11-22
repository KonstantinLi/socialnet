package ru.skillbox.socialnet.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.skillbox.socialnet.entity.postrelated.Post;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PostsRepository extends JpaRepository<Post, Long> {
    Optional<Post> findByIdAndIsDeleted(long id, boolean isDeleted);

    @Query(nativeQuery = true, value = """
            select
                p.*
            from
                posts p
                    inner join persons a
                        on p.author_id = a.id
                    left join (select
                                    pt.post_id,
                                    count(distinct t.id) count
                                from post2tag pt
                                    inner join tags t
                                        on pt.tag_id = t.id
                                        and (:tagsNull or t.tag in (:tags))
                                    group by pt.post_id) sort
                        on p.id = sort.post_id
                    left join post2tag pt
                            inner join tags t
                                on pt.tag_id = t.id
                        on p.id = pt.post_id
            where
                not p.is_deleted
                and (not p.is_blocked or p.author_id = :userId)
                and (cast(:author as varchar) is null
                    or a.first_name ilike concat('%', cast(:author as varchar), '%')
                    or a.last_name ilike concat('%', cast(:author as varchar), '%')
                    or concat(a.first_name,' ',a.last_name) ilike concat('%', cast(:author as varchar), '%')
                    or concat(a.last_name,' ',a.first_name) ilike concat('%', cast(:author as varchar), '%'))
                and (:dateFrom = 0 or extract(epoch FROM p.time) >= :dateFrom)
                and (:dateTo = 0 or extract(epoch FROM p.time) <= :dateTo)
                and (:tagsNull or t.tag in (:tags))
                and (cast(:text as text) is null
                    or p.post_text ilike concat('%', cast(:text as text), '%')
                    or p.title ilike concat('%', cast(:text as text), '%'))
            group by p.id, coalesce(sort.count,0)
            order by
                coalesce(sort.count,0) desc,
                min(p.time) desc
            """)
    Page<Post> findPostsByQuery(@Param("author") String author,
                                @Param("dateFrom") long dateFrom,
                                @Param("dateTo") long dateTo,
                                @Param("tagsNull") boolean tagsNull,
                                @Param("tags") String[] tags,
                                @Param("text") String text,
                                @Param("userId") long userId,
                                Pageable nextPage);

    long countByAuthorIdAndIsDeleted(
            long authorId, boolean isDeleted
    );
    List<Post> findAllByAuthorIdAndIsDeleted(
            long authorId, boolean isDeleted, Pageable pageable
    );

    long countByAuthorIdAndIsBlocked(
            long authorId, boolean isBlocked
    );
    List<Post> findAllByAuthorIdAndIsBlocked(
            long authorId, boolean isBlocked, Pageable pageable
    );

    long countByAuthorId(
            long authorId
    );
    List<Post> findAllByAuthorId(
            long authorId, Pageable pageable
    );

    long countByIsDeletedAndTimeGreaterThan(
            boolean isDeleted, LocalDateTime time
    );
    List<Post> findAllByIsDeletedAndTimeGreaterThan(
            boolean isDeleted, LocalDateTime time, Pageable pageable
    );

    List<Post> findAllByIsDeletedAndTimeDeleteLessThan(
            boolean isDeleted, LocalDateTime timeDelete
    );

    List<Post> findAllByIsDeletedAndTimeDelete(
            boolean isDeleted, LocalDateTime timeDelete
    );

    List<Post> findAllByIsDeleted(
            boolean isDeleted, Pageable pageable
    );
    long countByIsDeleted(boolean isDeleted);

    List<Post> findAllByIsDeletedAndIsBlocked(
            boolean isDeleted, boolean isBlocked, Pageable pageable
    );
    long countByIsDeletedAndIsBlocked(boolean isDeleted, boolean isBlocked);
}
