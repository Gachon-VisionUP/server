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
            @Parameter(hidden = true) @SessionAttribute(name = "userId", required = false) Long userId) {

        // 세션에서 userId가 없는 경우 (로그인하지 않은 상태)
        if (userId == null) {
            return ApiResponse.onFailure(GlobalErrorStatus._NOT_LOGIN);
        }

        return ApiResponse.onSuccess(userCommandService.userCreate(userId, request));
    }

    @GetMapping("/user-list")
    public ApiResponse<UserResponse.UserInfoList> getUserInfoList(
            @Parameter(hidden = true) @SessionAttribute(name = "userId", required = false) Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "8") int size) {

        // 세션에서 userId가 없는 경우 (로그인하지 않은 상태)
        if (userId == null) {
            return ApiResponse.onFailure(GlobalErrorStatus._NOT_LOGIN);
        }

        // 성공적으로 사용자 정보를 반환
        return ApiResponse.onSuccess(userQueryService.getUserInfoList(userId, page, size));
    }

    @GetMapping("/user-info/{targetId}")
    public ApiResponse<UserResponse.UserInfoDetail> getUserInfoDetail(
            @Parameter(hidden = true) @SessionAttribute(name = "userId", required = false) Long userId,
            @PathVariable(name = "targetId") Long targetId){

        // 세션에서 userId가 없는 경우 (로그인하지 않은 상태)
        if (userId == null) {
            return ApiResponse.onFailure(GlobalErrorStatus._NOT_LOGIN);
        }

        return ApiResponse.onSuccess(userQueryService.getUserInfoDetail(userId, targetId));
    }

    @PutMapping("/user-info/{targetId}")
    public ApiResponse<UserResponse.UpdateInformation> updateUserInfo(
            @Parameter(hidden = true) @SessionAttribute(name = "userId", required = false) Long userId,
            @PathVariable(name = "targetId") Long targetId,
            @RequestBody UserRequest.UpdateUserInfo request){

        // 세션에서 userId가 없는 경우 (로그인하지 않은 상태)
        if (userId == null) {
            return ApiResponse.onFailure(GlobalErrorStatus._NOT_LOGIN);
        }

        return ApiResponse.onSuccess(userCommandService.updateUserInfo(userId, targetId, request));
    }
}