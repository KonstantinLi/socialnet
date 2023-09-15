package ru.skillbox.socialnet.service.post;

import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;
import ru.skillbox.socialnet.dto.request.PostRq;
import ru.skillbox.socialnet.dto.response.CommonRsPostRs;
import ru.skillbox.socialnet.dto.response.ErrorRs;
import ru.skillbox.socialnet.dto.response.PostRs;
import ru.skillbox.socialnet.entity.post.Post;
import ru.skillbox.socialnet.mapper.PostMapper;
import ru.skillbox.socialnet.repository.PostsRepository;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UpdatePostByIdService extends AbstractPostsService {
    private final PostsRepository postsRepository;
    private final PostMapper postMapper = Mappers.getMapper(PostMapper.class);

    public ResponseEntity<?> updateById(String authorization, Long id, PostRq postRq) {
        try {
            getMyId(authorization);
        } catch (AuthenticationException e) {
            return new ResponseEntity<>(HttpStatusCode.valueOf(401));
        }

        Optional<Post> optionalPost = postsRepository.findById(id);

        if (optionalPost.isEmpty()) {
            return new ResponseEntity<>(
                    new ErrorRs("No record found"),
                    HttpStatusCode.valueOf(400)
            );
        }

        Post post = optionalPost.get();
        // TODO: UpdatePostByIdService updateById - test mapping
        postMapper.postRqToPost(postRq, post);

        postsRepository.save(post);

        // TODO: UpdatePostByIdService updateById - test mapping
        PostRs postRs = postMapper.postToPostRs(post);

        CommonRsPostRs commonRsPostRs = new CommonRsPostRs();
        commonRsPostRs.setData(postRs);
        // TODO: UpdatePostByIdService updateById - other response fields filling

        return new ResponseEntity<>(
                commonRsPostRs,
                HttpStatusCode.valueOf(200)
        );
    }
}
