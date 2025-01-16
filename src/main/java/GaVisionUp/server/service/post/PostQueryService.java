package GaVisionUp.server.service.post;

import GaVisionUp.server.entity.enums.Filter;
import GaVisionUp.server.web.dto.post.PostResponse;

public interface PostQueryService {

//    PostResponse.PreviewList getPostsPreviewList(String query, Filter filter, Long lastValue, int pageSize);
    PostResponse.PreviewList getPostsPreviewList();

    PostResponse.Detail getPost(Long postId);

}