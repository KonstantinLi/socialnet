package com.socialnet.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.socialnet.entity.postrelated.Post2Tag;

@Repository
public interface Post2tagRepository extends JpaRepository<Post2Tag, Long> {
    long countByPostId(long postId);
}
