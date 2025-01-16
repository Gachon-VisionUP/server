package GaVisionUp.server.service.post;

import GaVisionUp.server.entity.Post;
import GaVisionUp.server.entity.User;
import GaVisionUp.server.entity.enums.Filter;
import GaVisionUp.server.global.exception.RestApiException;
import GaVisionUp.server.global.exception.code.status.GlobalErrorStatus;
import GaVisionUp.server.repository.post.PostRepository;
import GaVisionUp.server.repository.user.UserRepository;
import GaVisionUp.server.web.dto.post.PostResponse;
import GaVisionUp.server.web.dto.user.UserRequest;
import GaVisionUp.server.web.dto.user.UserResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PostQueryServiceImpl implements PostQueryService {

    private final PostRepository postRepository;

    /*@Override
    public PostResponse.PreviewList getPostsPreviewList(String query, Filter filter, Long lastValue, int pageSize) {

        validateQuery(query);

        Pageable pageable = PageRequest.of(0, pageSize);

        return postRepository.findByQueryOrderByFilter(query, filter, lastValue, pageable);
    }*/

    @Override
    public PostResponse.PreviewList getPostsPreviewList() {

        List<Post> postList = postRepository.findAll();

        List<PostResponse.Preview> previewList = postList.stream()
                .map(post -> PostResponse.Preview.builder()
                        .postId(post.getId())
                        .title(post.getTitle())
                        .body(post.getBody())
                        .date(post.getDate())
                        .build())
                .toList();

        return PostResponse.PreviewList.builder().previewList(previewList).build();
    }

    /*private void validateQuery(String query) {
        if (query == null) {
            return; // query가 null이면 검사를 하지 않음.
        }

        int queryLength = query.length();

        if (queryLength < 2) {
            throw new RestApiException(GlobalErrorStatus._QUERY_SO_SHORT);
        }
        if (queryLength > 20) {
            throw new RestApiException(GlobalErrorStatus._QUERY_SO_LONG);
        }
    }*/

    @Override
    public PostResponse.Detail getPost(Long postId) {
        Post post = postRepository.findById(postId).
                orElseThrow(() -> new RestApiException(GlobalErrorStatus._POST_NOT_EXIST));

        return PostResponse.Detail.builder()
                .title(post.getTitle())
                .body(post.getBody())
                .date(post.getDate())
                .build();
    }
}