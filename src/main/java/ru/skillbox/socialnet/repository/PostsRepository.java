package ru.skillbox.socialnet.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.skillbox.socialnet.entity.post.Post;

@Repository
public interface PostsRepository extends JpaRepository<Post, Long> {

    @Query(nativeQuery = true, value = """
    select
        p.*
    from
        posts as p
        inner join persons as a
            on p.author_id = a.id
    where
        case when cast(:author as varchar) is null then true
            else (a.first_name ilike concat('%', cast(:author as varchar), '%')
                or a.last_name ilike concat('%', cast(:author as varchar), '%')) end
        and case when :dateFrom = 0 then true
            else extract(epoch FROM p.time) >= :dateFrom end
        and case when :dateTo = 0 then true
            else extract(epoch FROM p.time) <= :dateTo end
    """)
    Page<Post> findPostsByQuery(@Param("author") String author,
                                @Param("dateFrom") long dateFrom,
                                @Param("dateTo") long dateTo,
//                                @Param("tags") String[] tags,
//                                @Param("text") String text,
                                Pageable nextPage);

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
}
