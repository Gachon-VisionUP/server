package GaVisionUp.server.web.controller.user;

import GaVisionUp.server.entity.User;
import GaVisionUp.server.global.base.ApiResponse;
import GaVisionUp.server.global.exception.RestApiException;
import GaVisionUp.server.global.exception.code.status.GlobalErrorStatus;
import GaVisionUp.server.service.user.UserCommandService;
import GaVisionUp.server.service.user.UserQueryService;
import GaVisionUp.server.web.dto.user.UserRequest;
import GaVisionUp.server.web.dto.user.UserResponse;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admins")
public class AdminController {

    private final UserCommandService userCommandService;
    private final UserQueryService userQueryService;

    @PostMapping("/create")
    public ApiResponse<UserResponse.Create> userCreate(
            @RequestBody UserRequest.Create request,
            @Parameter(hidden = true) @SessionAttribute(name = "userId", required = false) Long sessionUserId,
            @RequestParam(name = "userId", required = false) Long userId) {

        validateUserIds(sessionUserId, userId);

        return ApiResponse.onSuccess(userCommandService.userCreate(userId, request));
    }

    @GetMapping("/user-list")
    public ApiResponse<UserResponse.UserInfoList> getUserInfoList(
            @Parameter(hidden = true) @SessionAttribute(name = "userId", required = false) Long sessionUserId,
            @RequestParam(name = "userId", required = false) Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "8") int size) {

        validateUserIds(sessionUserId, userId);

        // 성공적으로 사용자 정보를 반환
        return ApiResponse.onSuccess(userQueryService.getUserInfoList(userId, page, size));
    }

    @PutMapping("/user-info")
    public ApiResponse<UserResponse.UpdateInformation> updateUserInfo(
            @Parameter(hidden = true) @SessionAttribute(name = "userId", required = false) Long sessionUserId,
            @RequestParam(name = "userId", required = false) Long userId,
            @RequestParam(name = "targetId") Long targetId,
            @RequestBody UserRequest.UpdateUserInfo request){

        validateUserIds(sessionUserId, userId);

        return ApiResponse.onSuccess(userCommandService.updateUserInfo(userId, targetId, request));
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