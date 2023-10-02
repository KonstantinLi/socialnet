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
    Optional<Post> findByIdAndIsDeleted(long Id, boolean isDeleted);

    @Query(nativeQuery = true, value = """
            select
                p.*
            from
                posts p
                    inner join persons a
                        on p.author_id = a.id
                    left join post2tag sort_t
                        on p.id = sort_t.post_id
                    left join post2tag pt
                            inner join tags t
                                on pt.tag_id = t.id
                        on p.id = pt.post_id
            where
                not p.is_deleted
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
            group by p.id
            order by
                count(distinct sort_t.tag_id) desc,
                min(p.time) desc
            """)
    Page<Post> findPostsByQuery(@Param("author") String author,
                                @Param("dateFrom") long dateFrom,
                                @Param("dateTo") long dateTo,
                                @Param("tagsNull") boolean tagsNull,
                                @Param("tags") String[] tags,
                                @Param("text") String text,
                                Pageable nextPage);

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
}
