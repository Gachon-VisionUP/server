package GaVisionUp.server.web.controller.post;

import GaVisionUp.server.entity.enums.Filter;
import GaVisionUp.server.global.base.ApiResponse;
import GaVisionUp.server.global.exception.RestApiException;
import GaVisionUp.server.global.exception.code.status.GlobalErrorStatus;
import GaVisionUp.server.service.post.PostCommandService;
import GaVisionUp.server.service.post.PostQueryService;
import GaVisionUp.server.web.dto.post.PostRequest;
import GaVisionUp.server.web.dto.post.PostResponse;
import io.swagger.v3.oas.annotations.Parameter;
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
    public ApiResponse<PostResponse.AddPost> addPost(
            @Parameter(hidden = true) @SessionAttribute(name = "userId", required = false) Long sessionUserId,
            @RequestParam(name = "userId", required = false) Long userId,
            @RequestBody PostRequest.AddPost request) {

        validateUserIds(sessionUserId, userId);

        return ApiResponse.onSuccess(postCommandService.addPost(userId, request));
    }

    private void validateUserIds(Long sessionUserId, Long userId) {
        // 세션에서 userId가 없는 경우 (로그인하지 않은 상태)
        if (sessionUserId == null) {
            throw new RestApiException(GlobalErrorStatus._NOT_LOGIN);
        }

        // 요청으로 전달된 userId와 세션의 userId가 다를 경우 에러 발생
        if (userId != null && !sessionUserId.equals(userId)) {
            throw new RestApiException(GlobalErrorStatus._INVALID_USER);
        }
    }
}
