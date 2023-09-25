package ru.skillbox.socialnet.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.skillbox.socialnet.dto.PostRs;
import ru.skillbox.socialnet.dto.response.CommonRs;
import ru.skillbox.socialnet.dto.service.GetPostsSearchPs;
import ru.skillbox.socialnet.repository.PostsRepository;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PostsService {

    private final PostsRepository postsRepository;

    public CommonRs<List<PostRs>> getPostsByQuery(Long currentUserId,
                                                  GetPostsSearchPs getPostsSearchPs,
                                                  int offset,
                                                  int perPage) {
        Pageable nextPage = PageRequest.of(offset, perPage);
        postsRepository.findPostsByQuery(getPostsSearchPs, nextPage);
        CommonRs<List<PostRs>> result = new CommonRs<>();
        List<PostRs> data = new ArrayList<>();
        result.setData(data);
        return result;
    }
}
