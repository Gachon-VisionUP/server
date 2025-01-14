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
@RequestMapping("/api/users")
public class UserController {

    private final UserCommandService userCommandService;
    private final UserQueryService userQueryService;

    @PostMapping("/login")
    public ApiResponse<UserResponse.Login> login(@RequestBody UserRequest.Login request, HttpServletRequest httpServletRequest) {
        // 사용자 인증 로직
        User user = userQueryService.login(request);

        // 로그인 성공 시 세션 생성
        httpServletRequest.getSession().invalidate();
        HttpSession session = httpServletRequest.getSession(true);
        session.setAttribute("userId", user.getId());
        session.setMaxInactiveInterval(1800); // 30분 유지

        sessionList.put(session.getId(), session);

        // 성공 응답 반환
        return ApiResponse.onSuccess(UserResponse.Login.builder().user(user).build());
    }

    @GetMapping("/logout")
    public ApiResponse<?> logout(HttpServletRequest request, Model model) {
        HttpSession session = request.getSession(false); // 세션이 없으면 null 반환
        if (session != null) {
            sessionList.remove(session.getId());
            session.invalidate(); // 세션 무효화
        }


        // 성공 응답 반환
        return ApiResponse.onSuccess("로그아웃 성공!");
    }

    @GetMapping("/info")
    public ApiResponse<UserResponse.Information> getUserInformation(
            @Parameter(hidden = true) @SessionAttribute(name = "userId", required = false) Long sessionUserId,
            @RequestParam(name = "userId", required = false) Long userId) {

        validateUserIds(sessionUserId, userId);

        // 성공적으로 사용자 정보를 반환
        return ApiResponse.onSuccess(userQueryService.getUserInformation(userId));
    }

    @PutMapping("/password")
    public ApiResponse<UserResponse.UpdateInformation> changePassword(
            @Parameter(hidden = true) @SessionAttribute(name = "userId", required = false) Long sessionUserId,
            @RequestParam(name = "userId", required = false) Long userId,
            @RequestBody UserRequest.ChangePassword request){

        validateUserIds(sessionUserId, userId);

        return ApiResponse.onSuccess(userCommandService.changePassword(userId, request));
    }

    @PutMapping("/image")
    public ApiResponse<UserResponse.UpdateInformation> changeImage(
            @Parameter(hidden = true) @SessionAttribute(name = "userId", required = false) Long sessionUserId,
            @RequestParam(name = "userId", required = false) Long userId,
            @RequestBody String changeImageUrl){

        validateUserIds(sessionUserId, userId);

        return ApiResponse.onSuccess(userCommandService.changeImage(userId, changeImageUrl));
    }

    public static Hashtable sessionList = new Hashtable();

    @GetMapping("/session-list")
    @ResponseBody
    public Map<String, String> sessionList() {
        Enumeration elements = sessionList.elements();
        Map<String, String> lists = new HashMap<>();
        while(elements.hasMoreElements()) {
            HttpSession session = (HttpSession)elements.nextElement();
            lists.put(session.getId(), String.valueOf(session.getAttribute("userId")));
        }
        return lists;
    }

    // ✅ Expo 푸쉬 토큰 저장 API
    @PostMapping("/{userId}/push-token")
    public ResponseEntity<Void> updatePushToken(@PathVariable Long userId, @RequestBody String pushToken) {
        userCommandService.updatePushToken(userId, pushToken);
        return ResponseEntity.ok().build();
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