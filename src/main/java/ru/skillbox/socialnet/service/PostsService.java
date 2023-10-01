package ru.skillbox.socialnet.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.skillbox.socialnet.dto.response.PostRs;
import ru.skillbox.socialnet.dto.response.CommonRs;
import ru.skillbox.socialnet.dto.service.GetPostsSearchPs;
import ru.skillbox.socialnet.entity.postrelated.Post;
import ru.skillbox.socialnet.repository.PostsRepository;
import ru.skillbox.socialnet.util.mapper.PostMapper;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PostsService {

    private final PostsRepository postsRepository;

    @Transactional
    public CommonRs<List<PostRs>> getPostsByQuery(Long currentUserId,
                                                  GetPostsSearchPs getPostsSearchPs,
                                                  int offset,
                                                  int perPage) {
        Pageable nextPage = PageRequest.of(offset, perPage);
        CommonRs<List<PostRs>> result = new CommonRs<>();
        Page<Post> postPage = postsRepository.findPostsByQuery(getPostsSearchPs.getAuthor(),
                getPostsSearchPs.getDateFrom(),
                getPostsSearchPs.getDateTo(),
//                getPostsSearchPs.getTags(),
//                getPostsSearchPs.getText(),
                nextPage);
        result.setData(PostMapper.INSTANCE.toRsList(postPage.getContent()));
        result.setTotal(postPage.getTotalElements());
        result.setItemPerPage(postPage.getContent().size());
        result.setPerPage(perPage);
        result.setOffset(offset);

        return result;
    }
}
