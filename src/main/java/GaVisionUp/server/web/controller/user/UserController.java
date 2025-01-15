package GaVisionUp.server.web.controller.user;

import GaVisionUp.server.entity.User;
import GaVisionUp.server.global.base.ApiResponse;
import GaVisionUp.server.global.exception.RestApiException;
import GaVisionUp.server.global.exception.code.status.GlobalErrorStatus;
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
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {

    private final UserCommandService userCommandService;
    private final UserQueryService userQueryService;

    @PostMapping("/login")
    @Operation(summary = "로그인 API", description = "로그인 시 세션을 생성하여 저장합니다.")
    public ApiResponse<UserResponse.Login> login(@Valid @RequestBody UserRequest.Login request, HttpServletRequest httpServletRequest) {
        // 사용자 인증 로직
        User user = userQueryService.login(request);

        // 현재 세션 확인
        HttpSession currentSession = httpServletRequest.getSession(false);
        if (currentSession != null) {
            Object existingUserId = currentSession.getAttribute("userId");
            if (existingUserId != null) {
                // 이미 로그인된 사용자라면 에러 반환
                if (!existingUserId.equals(user.getId())) {
                    return ApiResponse.onFailure(GlobalErrorStatus._ALREADY_LOGIN);
                }
            }
            currentSession.invalidate(); // 기존 세션 무효화
        }

        // 새 세션 생성 및 사용자 정보 저장
        HttpSession newSession = httpServletRequest.getSession(true);
        newSession.setAttribute("userId", user.getId());
        newSession.setMaxInactiveInterval(1800); // 30분 유지

        sessionList.put(newSession.getId(), newSession);

        // 성공 응답 반환
        return ApiResponse.onSuccess(UserResponse.Login.builder().user(user).build());
    }

    @GetMapping("/logout")
    @Operation(summary = "로그아웃 API", description = "저장하고있던 세션을 무효화 시킵니다.")
    public ApiResponse<?> logout(HttpServletRequest request) {
        HttpSession session = request.getSession(false); // 세션이 없으면 null 반환
        if (session != null) {
            sessionList.remove(session.getId());
            session.invalidate(); // 세션 무효화
        } else {
            return ApiResponse.onFailure(GlobalErrorStatus._ALREADY_LOGOUT);
        }

        // 성공 응답 반환
        return ApiResponse.onSuccess("로그아웃 성공!");
    }

    @GetMapping("/info")
    @Operation(summary = "정보 조회 API", description = "사용자의 정보를 조회합니다.")
    public ApiResponse<UserResponse.Information> getUserInformation(
            @Parameter(hidden = true) @SessionAttribute(name = "userId", required = false) Long userId) {

        // 세션에서 userId가 없는 경우 (로그인하지 않은 상태)
        if (userId == null) {
            return ApiResponse.onFailure(GlobalErrorStatus._NOT_LOGIN);
        }


        // 성공적으로 사용자 정보를 반환
        return ApiResponse.onSuccess(userQueryService.getUserInformation(userId));
    }

    @PutMapping("/password")
    @Operation(summary = "비밀번호 변경 API", description = "비밀번호를 변경합니다.(단, 기존 비밀번호와 같을 시 수정불가.)")
    public ApiResponse<UserResponse.UpdateInformation> changePassword(
            @Parameter(hidden = true) @SessionAttribute(name = "userId", required = false) Long userId,
            @Valid @RequestBody UserRequest.ChangePassword request){

        // 세션에서 userId가 없는 경우 (로그인하지 않은 상태)
        if (userId == null) {
            return ApiResponse.onFailure(GlobalErrorStatus._NOT_LOGIN);
        }

        return ApiResponse.onSuccess(userCommandService.changePassword(userId, request));
    }

    @PutMapping("/image")
    @Operation(summary = "캐릭터 변경 API", description = "8가지 캐릭터 중에 선택하여 변경, 작성 시 \"\" 없이 man-01이라고 바로 쓰면 됨.")
    public ApiResponse<UserResponse.UpdateInformation> changeImage(
            @Parameter(hidden = true) @SessionAttribute(name = "userId", required = false) Long userId,
            @RequestBody String changeImageUrl){

        // 세션에서 userId가 없는 경우 (로그인하지 않은 상태)
        if (userId == null) {
            return ApiResponse.onFailure(GlobalErrorStatus._NOT_LOGIN);
        }

        return ApiResponse.onSuccess(userCommandService.changeImage(userId, changeImageUrl));
    }

    public static Hashtable sessionList = new Hashtable();

    @GetMapping("/session-list")
    @Operation(summary = "세션 리스트 조회 API", description = "현재 로그인 중인 모든 세션 id를 조회합니다.")
    @ResponseBody
    public ApiResponse<Map<String, String>> sessionList() {
        Enumeration<HttpSession> elements = sessionList.elements();
        Map<String, String> lists = new HashMap<>();
        while (elements.hasMoreElements()) {
            HttpSession session = elements.nextElement();
            try {
                // 무효화된 세션 접근 시 예외 처리
                lists.put(session.getId(), String.valueOf(session.getAttribute("userId")));
            } catch (IllegalStateException e) {
                // 무효화된 세션은 건너뜀
                log.warn("Invalidated session detected: {}", session.getId(), e);
            }
        }
        return ApiResponse.onSuccess(lists);
    }

    @GetMapping("/session/check")
    @Operation(summary = "세션 체크 API", description = "세션이 활성화 되어있는지 확인합니다.")
    public ApiResponse<String> checkSession(HttpServletRequest request) {
        HttpSession session = request.getSession(false); // 기존 세션이 없으면 null 반환
        if (session == null) {
            return ApiResponse.onFailure(GlobalErrorStatus._UNAUTHORIZED);
        }
        return ApiResponse.onSuccess("Session is active: " + session.getId());
    }

    // ✅ Expo 푸쉬 토큰 저장 API
    @PostMapping("/{userId}/push-token")
    @Operation(summary = "푸쉬 토큰 저장 API", description = "입력한 Token으로 푸쉬 토큰을 업데이트 합니다.")
    @Parameters({
            @Parameter(name = "userId", description = "사용자 id")
    })
    public ApiResponse<Void> updatePushToken(@PathVariable Long userId, @RequestBody String pushToken) {
        userCommandService.updatePushToken(userId, pushToken);
        return ApiResponse.onSuccess(null);
    }
}