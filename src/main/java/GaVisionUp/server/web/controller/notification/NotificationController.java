package GaVisionUp.server.web.controller.notification;

import GaVisionUp.server.entity.Notification;
import GaVisionUp.server.global.base.ApiResponse;
import GaVisionUp.server.global.exception.code.status.GlobalErrorStatus;
import GaVisionUp.server.service.notification.ExpoNotificationService;
import GaVisionUp.server.service.notification.NotificationService;
import GaVisionUp.server.service.user.UserQueryService;
import GaVisionUp.server.web.dto.notification.NotificationResponse;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;
@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;
    private final ExpoNotificationService expoNotificationService;
    private final UserQueryService userQueryService;

    // ✅ 현재 로그인한 유저의 모든 알림 조회 (세션 userId 검증 추가)
    @GetMapping("/all")
    public ResponseEntity<List<NotificationResponse>> getAllNotifications(
            @Parameter(hidden = true) @SessionAttribute(name = "userId", required = false) Long sessionUserId,
            @RequestParam(name = "userId", required = false) Long userId) {
        // 세션에서 userId가 없는 경우 (로그인하지 않은 상태)
        if (sessionUserId == null) {
            return ResponseEntity.badRequest().build();
        }

        // 요청으로 전달된 userId와 세션의 userId가 다를 경우 에러 반환
        if (userId != null && !sessionUserId.equals(userId)) {
            return ResponseEntity.badRequest().build();
        }

        List<NotificationResponse> notifications = notificationService.getAllNotifications(sessionUserId)
                .stream()
                .map(NotificationResponse::new)
                .collect(Collectors.toList());

        return ResponseEntity.ok(notifications);
    }

    // ✅ 현재 로그인한 유저의 읽지 않은 알림 조회 (세션 userId 검증 추가)
    @GetMapping("/unread")
    public ResponseEntity<List<NotificationResponse>> getUnreadNotifications(
            @Parameter(hidden = true) @SessionAttribute(name = "userId", required = false) Long sessionUserId,
            @RequestParam(name = "userId", required = false) Long userId) {

        // 세션에서 userId가 없는 경우 (로그인하지 않은 상태)
        if (sessionUserId == null) {
            return ResponseEntity.badRequest().build();
        }

        // 요청으로 전달된 userId와 세션의 userId가 다를 경우 에러 반환
        if (userId != null && !sessionUserId.equals(userId)) {
            return ResponseEntity.badRequest().build();
        }

        List<NotificationResponse> notifications = notificationService.getUnreadNotifications(sessionUserId)
                .stream()
                .map(NotificationResponse::new)
                .collect(Collectors.toList());

        return ResponseEntity.ok(notifications);
    }

    // ✅ 특정 알림 읽음 처리 (세션 userId 검증 추가)
    @PostMapping("/{notificationId}/read")
    public ResponseEntity<Void> markNotificationAsRead(
            @PathVariable Long notificationId,
            @Parameter(hidden = true) @SessionAttribute(name = "userId", required = false) Long sessionUserId,
            @RequestParam(name = "userId", required = false) Long userId) {

        // 세션에서 userId가 없는 경우 (로그인하지 않은 상태)
        if (sessionUserId == null) {
            return ResponseEntity.badRequest().build();
        }

        // 요청으로 전달된 userId와 세션의 userId가 다를 경우 에러 반환
        if (userId != null && !sessionUserId.equals(userId)) {
            return ResponseEntity.badRequest().build();
        }
        // ✅ 해당 알림이 로그인한 유저의 것인지 검증
        Notification notification = notificationService.getNotificationById(notificationId);
        if (!notification.getUser().getId().equals(sessionUserId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build(); // ✅ 접근 권한 없음
        }

        notificationService.markAsRead(notificationId);
        return ResponseEntity.ok().build();
    }

    // ✅ 현재 로그인한 유저의 모든 알림을 읽음 처리 (세션 userId 검증 추가)
    @PostMapping("/mark-all-read")
    public ResponseEntity<Void> markAllNotificationsAsRead(
            @Parameter(hidden = true) @SessionAttribute(name = "userId", required = false) Long sessionUserId,
            @RequestParam(name = "userId", required = false) Long userId) {

        // 세션에서 userId가 없는 경우 (로그인하지 않은 상태)
        if (sessionUserId == null) {
            return ResponseEntity.badRequest().build();
        }

        // 요청으로 전달된 userId와 세션의 userId가 다를 경우 에러 반환
        if (userId != null && !sessionUserId.equals(userId)) {
            return ResponseEntity.badRequest().build();
        }

        notificationService.markAllAsRead(sessionUserId);
        return ResponseEntity.ok().build();
    }

    // ✅ 푸쉬 알림 전송 (세션 userId 검증 필요 없음)
    @PostMapping("/send")
    public ApiResponse<String> sendNotificationToAllUsers(
            @RequestParam String title,
            @RequestParam String body) {

        List<String> tokens = userQueryService.getAllExpoPushTokens();
        expoNotificationService.sendNotificationToAllUsers(tokens, title, body);
        return ApiResponse.onSuccess("Notifications sent successfully!");
    }
}