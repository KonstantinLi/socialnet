package ru.skillbox.socialnet.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.skillbox.socialnet.dto.service.GetPostsSearchPs;
import ru.skillbox.socialnet.entity.post.Post;

import java.util.List;

@Repository
public interface PostsRepository extends JpaRepository<Post, Long> {
    @Query(nativeQuery = true, value = "select * from posts")
    List<Post> findPostsByQuery(GetPostsSearchPs getPostsSearchPs, Pageable nextPage);
}
