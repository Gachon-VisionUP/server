package GaVisionUp.server.web.controller.user;

import GaVisionUp.server.entity.User;
import GaVisionUp.server.global.base.ApiResponse;
import GaVisionUp.server.global.exception.RestApiException;
import GaVisionUp.server.global.exception.code.status.GlobalErrorStatus;
import GaVisionUp.server.service.google.GoogleUserService;
import GaVisionUp.server.service.user.UserCommandService;
import GaVisionUp.server.service.user.UserQueryService;
import GaVisionUp.server.web.dto.user.UserRequest;
import GaVisionUp.server.web.dto.user.UserResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
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
    private final GoogleUserService googleUserService;

    @PostMapping("/create")
    @Operation(summary = "계정 생성 API", description = "사용자 계정을 생성합니다.")
    public ApiResponse<UserResponse.Create> userCreate(
            @Valid @RequestBody UserRequest.Create request,
            @Parameter(hidden = true) @SessionAttribute(name = "userId", required = false) Long userId) {

        // 세션에서 userId가 없는 경우 (로그인하지 않은 상태)
        if (userId == null) {
            return ApiResponse.onFailure(GlobalErrorStatus._NOT_LOGIN);
        }

        googleUserService.syncDatabaseToGoogleSheet();

        return ApiResponse.onSuccess(userCommandService.userCreate(userId, request));
    }

    /*@GetMapping("/user-list")
    @Operation(summary = "구성원 전체 조회 API", description = "모든 구성원 목록을 조회합니다.(페이징)")
    @Parameters({
            @Parameter(name = "page", description = "구성원 목록 페이지(0부터 시작)"),
            @Parameter(name = "size", description = "한 페이지에 보여줄 구성원 목록의 크기")
    })
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
    }*/

    @GetMapping("/user-list")
    @Operation(summary = "구성원 전체 조회 API", description = "모든 구성원 목록을 조회합니다.")
    public ApiResponse<UserResponse.UserInfoList> getUserInfoList(
            @Parameter(hidden = true) @SessionAttribute(name = "userId", required = false) Long userId) {

        // 세션에서 userId가 없는 경우 (로그인하지 않은 상태)
        if (userId == null) {
            return ApiResponse.onFailure(GlobalErrorStatus._NOT_LOGIN);
        }

        // 성공적으로 사용자 정보를 반환
        return ApiResponse.onSuccess(userQueryService.getUserInfoList(userId));
    }

    @GetMapping("/user-info/{targetId}")
    @Operation(summary = "구성원 정보 조회 API", description = "특정 구성원의 정보를 조회합니다. 정보 수정과 같은 페이지")
    @Parameters({
            @Parameter(name = "targetId", description = "조회하려는 타겟의 id")
    })
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
    @Operation(summary = "구성원 정보 수정 API", description = "특정 구성원의 정보를 수정합니다. 구성원 정보 조회 API를 통해 가져온 정보를 바탕으로 수정")
    @Parameters({
            @Parameter(name = "targetId", description = "조회하려는 타겟의 id"),
    })
    public ApiResponse<UserResponse.UpdateInformation> updateUserInfo(
            @Parameter(hidden = true) @SessionAttribute(name = "userId", required = false) Long userId,
            @PathVariable(name = "targetId") Long targetId,
            @Valid @RequestBody UserRequest.UpdateUserInfo request){

        // 세션에서 userId가 없는 경우 (로그인하지 않은 상태)
        if (userId == null) {
            return ApiResponse.onFailure(GlobalErrorStatus._NOT_LOGIN);
        }

        googleUserService.syncDatabaseToGoogleSheet();

        return ApiResponse.onSuccess(userCommandService.updateUserInfo(userId, targetId, request));
    }
}