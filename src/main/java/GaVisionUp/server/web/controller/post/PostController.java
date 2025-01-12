package GaVisionUp.server.web.controller.post;

import GaVisionUp.server.entity.enums.Filter;
import GaVisionUp.server.global.base.ApiResponse;
import GaVisionUp.server.service.post.PostCommandService;
import GaVisionUp.server.service.post.PostQueryService;
import GaVisionUp.server.web.dto.post.PostRequest;
import GaVisionUp.server.web.dto.post.PostResponse;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/posts")
public class PostController {

    private final PostQueryService postQueryService;
    private final PostCommandService postCommandService;

    @GetMapping()
    public ApiResponse<PostResponse.PreviewList> getPostsPreviewList(
            @RequestParam(required = false) String query,
            @RequestParam(defaultValue = "LATEST") Filter filter,
            @RequestParam(required = false) @Min(value = 0, message = "lastValue는 0 이상 입니다.") Long lastValue,
            @RequestParam(value = "page", defaultValue = "4") @Min(value = 1, message = "pageSize는 0보다 커야 합니다.") int pageSize
    ){
        System.out.println("컨트롤러임");
        return ApiResponse.onSuccess(postQueryService.getPostsPreviewList(query, filter, lastValue, pageSize));
    }

    @GetMapping("/{postId}")
    public ApiResponse<PostResponse.Detail> getPost(
            @PathVariable Long postId
    ) {
        return ApiResponse.onSuccess(postQueryService.getPost(postId));
    }

    @PostMapping("/add")
    public ApiResponse<PostResponse.AddPost> addPost(@RequestBody PostRequest.AddPost request) {
        return ApiResponse.onSuccess(postCommandService.addPost(request));
    }
}
