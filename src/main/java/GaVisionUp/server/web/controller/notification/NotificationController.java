package GaVisionUp.server.web.controller.notification;

import GaVisionUp.server.entity.Notification;
import GaVisionUp.server.global.base.ApiResponse;
import GaVisionUp.server.global.exception.code.status.GlobalErrorStatus;
import GaVisionUp.server.service.notification.ExpoNotificationService;
import GaVisionUp.server.service.notification.NotificationService;
import GaVisionUp.server.service.user.UserQueryService;
import GaVisionUp.server.web.dto.notification.NotificationResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
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

    // ✅ 현재 로그인한 유저의 모든 알림 조회
    @GetMapping("/all")
    @Operation(summary = "모든 알림 조회 API", description = "현재 로그인한 유저의 모든 알림을 조회합니다.")
    public ResponseEntity<List<NotificationResponse>> getAllNotifications(
            @Parameter(hidden = true) @SessionAttribute(name = "userId", required = false) Long userId) {
        // 세션에서 userId가 없는 경우 (로그인하지 않은 상태)
        if (userId == null) {
            return ResponseEntity.badRequest().build();
        }


        List<NotificationResponse> notifications = notificationService.getAllNotifications(userId)
                .stream()
                .map(NotificationResponse::new)
                .collect(Collectors.toList());

        return ResponseEntity.ok(notifications);
    }

    // ✅ 현재 로그인한 유저의 읽지 않은 알림 조회
    @GetMapping("/unread")
    @Operation(summary = "읽지 않은 알림 조회 API", description = "현재 로그인한 유저의 읽지 않은 알림을 조회합니다.")
    public ResponseEntity<List<NotificationResponse>> getUnreadNotifications(
            @Parameter(hidden = true) @SessionAttribute(name = "userId", required = false) Long userId) {

        // 세션에서 userId가 없는 경우 (로그인하지 않은 상태)
        if (userId == null) {
            return ResponseEntity.badRequest().build();
        }

        List<NotificationResponse> notifications = notificationService.getUnreadNotifications(userId)
                .stream()
                .map(NotificationResponse::new)
                .collect(Collectors.toList());

        return ResponseEntity.ok(notifications);
    }

    // ✅ 특정 알림 읽음 처리
    @PostMapping("/{notificationId}/read")
    @Operation(summary = "알림 읽음 처리 API", description = "특정 알림을 읽음 처리합니다.")
    @Parameters({
            @Parameter(name = "notificationId", description = "읽음 처리할 알림 id")
    })
    public ResponseEntity<Void> markNotificationAsRead(
            @PathVariable Long notificationId,
            @Parameter(hidden = true) @SessionAttribute(name = "userId", required = false) Long userId) {

        // 세션에서 userId가 없는 경우 (로그인하지 않은 상태)
        if (userId == null) {
            return ResponseEntity.badRequest().build();
        }

        // ✅ 해당 알림이 로그인한 유저의 것인지 검증
        Notification notification = notificationService.getNotificationById(notificationId);
        if (!notification.getUser().getId().equals(userId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build(); // ✅ 접근 권한 없음
        }

        notificationService.markAsRead(notificationId);
        return ResponseEntity.ok().build();
    }

    // ✅ 현재 로그인한 유저의 모든 알림을 읽음 처리
    @PostMapping("/mark-all-read")
    @Operation(summary = "모든 알림 읽음 처리 API", description = "현재 로그인한 유저의 모든 알림을 읽음 처리 합니다.")
    public ResponseEntity<Void> markAllNotificationsAsRead(
            @Parameter(hidden = true) @SessionAttribute(name = "userId", required = false) Long userId) {

        // 세션에서 userId가 없는 경우 (로그인하지 않은 상태)
        if (userId == null) {
            return ResponseEntity.badRequest().build();
        }

        notificationService.markAllAsRead(userId);
        return ResponseEntity.ok().build();
    }

    // ✅ 푸쉬 알림 전송
    @PostMapping("/send")
    public ApiResponse<String> sendNotificationToAllUsers(
            @RequestParam String title,
            @RequestParam String body) {

        List<String> tokens = userQueryService.getAllExpoPushTokens();
        expoNotificationService.sendNotificationToAllUsers(tokens, title, body);
        return ApiResponse.onSuccess("Notifications sent successfully!");
    }
}