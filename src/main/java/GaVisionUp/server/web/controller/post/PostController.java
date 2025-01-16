package GaVisionUp.server.web.controller.post;

import GaVisionUp.server.entity.enums.Filter;
import GaVisionUp.server.global.base.ApiResponse;
import GaVisionUp.server.global.exception.RestApiException;
import GaVisionUp.server.global.exception.code.status.GlobalErrorStatus;
import GaVisionUp.server.service.post.PostCommandService;
import GaVisionUp.server.service.post.PostQueryService;
import GaVisionUp.server.web.dto.post.PostRequest;
import GaVisionUp.server.web.dto.post.PostResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/posts")
public class PostController {

    private final PostQueryService postQueryService;
    private final PostCommandService postCommandService;

    /*@GetMapping()
    @Operation(summary = "게시글 목록 조회 API", description = "게시글 목록을 조회합니다.(무한스크롤)")
    @Parameters({
            @Parameter(name = "query", description = "검색어"),
            @Parameter(name = "filter", description = "정렬기준"),
            @Parameter(name = "lastValue", description = "마지막 값, 다음 페이지 조회 시 사용"),
            @Parameter(name = "page", description = "한 번에 보여줄 게시글 수")
    })
    public ApiResponse<PostResponse.PreviewList> getPostsPreviewList(
            @RequestParam(required = false) String query,
            @RequestParam(defaultValue = "LATEST") Filter filter,
            @RequestParam(required = false) @Min(value = 0, message = "lastValue는 0 이상 입니다.") Long lastValue,
            @RequestParam(value = "page", defaultValue = "4") @Min(value = 1, message = "pageSize는 0보다 커야 합니다.") int pageSize
    ){
        System.out.println("컨트롤러임");
        return ApiResponse.onSuccess(postQueryService.getPostsPreviewList(query, filter, lastValue, pageSize));
    }*/

    @GetMapping()
    @Operation(summary = "게시글 목록 조회 API", description = "게시글 목록 전체를 조회합니다.")
    public ApiResponse<PostResponse.PreviewList> getPostsPreviewList(){
        return ApiResponse.onSuccess(postQueryService.getPostsPreviewList());
    }

    @GetMapping("/{postId}")
    @Operation(summary = "게시글 조회 API", description = "특정 게시글을 조회합니다.")
    @Parameters({
            @Parameter(name = "postId", description = "타겟 게시글의 id"),
    })
    public ApiResponse<PostResponse.Detail> getPost(
            @PathVariable Long postId
    ) {
        return ApiResponse.onSuccess(postQueryService.getPost(postId));
    }

    @PostMapping("/add")
    @Operation(summary = "게시글 생성 API", description = "게시글을 생성합니다.(관리자만 가능)")
    public ApiResponse<PostResponse.AddPost> addPost(
            @Parameter(hidden = true) @SessionAttribute(name = "userId", required = false) Long userId,
            @Valid @RequestBody PostRequest.AddPost request) {

        // 세션에서 userId가 없는 경우 (로그인하지 않은 상태)
        if (userId == null) {
            return ApiResponse.onFailure(GlobalErrorStatus._NOT_LOGIN);
        }

        return ApiResponse.onSuccess(postCommandService.addPost(userId, request));
    }
}
