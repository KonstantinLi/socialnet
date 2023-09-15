package ru.skillbox.socialnet.service.post;

import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;

import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import ru.skillbox.socialnet.dto.request.PostRq;
import ru.skillbox.socialnet.dto.response.ErrorRs;
import ru.skillbox.socialnet.entity.Person;
import ru.skillbox.socialnet.entity.post.Post;
import ru.skillbox.socialnet.entity.post.Tag;
import ru.skillbox.socialnet.mapper.PostMapper;
import ru.skillbox.socialnet.repository.PersonsRepository;
import ru.skillbox.socialnet.repository.PostsRepository;
import ru.skillbox.socialnet.repository.TagsRepository;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CreatePostService extends AbstractPostsService {
    private final PostsRepository postsRepository;
    private final TagsRepository tagsRepository;
    private final PersonsRepository personsRepository;
    private final PostMapper postMapper = Mappers.getMapper(PostMapper.class);

    public ResponseEntity<?> createPost(String authorization, Long publishDate, Long id, PostRq postRq) {
        try {
            getMyId(authorization);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatusCode.valueOf(401));
        }

        Optional<Person> optionalPerson;

        try {
            optionalPerson = personsRepository.findById(id);
        } catch (Exception e) {
            return new ResponseEntity<>(
                    new ErrorRs(e.getMessage()), HttpStatusCode.valueOf(500)
            );
        }

        if (optionalPerson.isEmpty()) {
            return new ResponseEntity<>(
                    new ErrorRs(ERROR_NO_RECORD_FOUND, "Post author record " + id + " not found"),
                    HttpStatusCode.valueOf(400)
            );
        }

        LocalDateTime postTime;

        if (publishDate == null) {
            postTime = LocalDateTime.now();
        } else {
            postTime = LocalDateTime.ofInstant(new Date(publishDate).toInstant(), ZoneOffset.systemDefault());
        }

        Post post = new Post();

        try {
            fillTags(postMapper.postRqToPost(postRq, post));
            post.setAuthor(optionalPerson.get());
            post.setTime(postTime);
        } catch (Exception e) {
            return new ResponseEntity<>(
                    new ErrorRs(e.getMessage()), HttpStatusCode.valueOf(500)
            );
        }

        post.setIsBlocked(false);
        post.setIsDeleted(false);

        try {
            postsRepository.save(post);
        } catch (Exception e) {
            return new ResponseEntity<>(
                    new ErrorRs(e.getMessage()), HttpStatusCode.valueOf(500)
            );
        }

        return null;
    }

    /**
     * Fills tags list of the post entity with correct tag entities, found by tag string.
     *
     * @param post Target post entity
     * @return The post entity given as parameter
     */
    private Post fillTags(Post post) {
        post.setTags(
                post.getTags().stream().map(tag -> {
                    Optional<Tag> optionalTag = tagsRepository.findByTag(tag.getTag());

                    if (optionalTag.isPresent()) {
                        return optionalTag.get();
                    }

                    tag.setId(null);
                    return tagsRepository.save(tag);
                }).toList()
        );

        return post;
    }
}
