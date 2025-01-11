package GaVisionUp.server.repository.post;

import GaVisionUp.server.entity.Post;
import GaVisionUp.server.entity.enums.Filter;
import GaVisionUp.server.global.exception.RestApiException;
import GaVisionUp.server.global.exception.code.status.GlobalErrorStatus;
import GaVisionUp.server.web.dto.post.PostResponse;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Collectors;

import static GaVisionUp.server.entity.QPost.post;


@RequiredArgsConstructor
@Repository
public class PostCustomRepositoryImpl implements PostCustomRepository {
    private final JPAQueryFactory queryFactory;

    @Override
    public PostResponse.PreviewList findByQueryOrderByFilter(String query, Filter filter, Long lastValue, Pageable pageable) {
        List<Post> results = queryFactory
                .selectFrom(post)
                .where(lastPostValue(filter, lastValue), searchPost(query))
                .orderBy(getOrderSpecifier(filter))
                .limit(pageable.getPageSize() + 1)
                .fetch();

        boolean hasNext = checkLastPage(pageable, results);
        if (hasNext) {
            results = results.subList(0, pageable.getPageSize()); // 마지막 요소 제외
        }

        List<PostResponse.Preview> previewList = results.stream()
                .map(post -> PostResponse.Preview.builder()
                                .postId(post.getId())
                                .title(post.getTitle())
                                .body(post.getBody())
                                .date(post.getDate())
                                .build())
                .collect(Collectors.toList());

        return PostResponse.PreviewList.builder()
                .previewList(previewList)
                .lastValue(getLastValue(results, filter))
                .hasNext(hasNext)
                .build();
    }

    private boolean checkLastPage(Pageable pageable, List<Post> postList) {
        return postList.size() > pageable.getPageSize();
    }

    private BooleanExpression searchPost(String query) {
        if (query == null) {
            return null;
        }
        return post.title.containsIgnoreCase(query)
                .or(post.body.containsIgnoreCase(query));
    }

    private BooleanExpression lastPostValue(Filter filter, Long lastValue) {
        if (lastValue == null || lastValue == 0) {
            return null;
        }
        return switch (filter) {
            case LATEST -> post.id.lt(lastValue);
            case OLDEST -> post.id.gt(lastValue);
            default -> throw new RestApiException(GlobalErrorStatus._INVALID_FILTER);
        };
    }

    private OrderSpecifier<?> getOrderSpecifier(Filter filter) {
        if (filter == null) {
            return null;
        }
        return switch (filter) {
            case LATEST -> post.id.desc();
            case OLDEST -> post.id.asc();
            default -> throw new RestApiException(GlobalErrorStatus._INVALID_FILTER);
        };
    }

    private Long getLastValue(List<Post> results, Filter filter) {
        if (results.isEmpty()) {
            return null;
        }
        return switch (filter) {
            case LATEST, OLDEST -> results.get(results.size() - 1).getId();
        };
    }
}

