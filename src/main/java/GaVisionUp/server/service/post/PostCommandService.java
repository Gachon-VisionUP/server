package GaVisionUp.server.service.post;

import GaVisionUp.server.entity.enums.Filter;
import GaVisionUp.server.web.dto.post.PostRequest;
import GaVisionUp.server.web.dto.post.PostResponse;

public interface PostCommandService {

    PostResponse.AddPost addPost(PostRequest.AddPost request);

}