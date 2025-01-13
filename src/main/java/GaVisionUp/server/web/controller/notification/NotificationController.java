package GaVisionUp.server.web.controller.notification;

import GaVisionUp.server.entity.Notification;
import GaVisionUp.server.global.base.ApiResponse;
import GaVisionUp.server.service.notification.ExpoNotificationService;
import GaVisionUp.server.service.notification.NotificationService;
import GaVisionUp.server.service.user.UserQueryService;
import GaVisionUp.server.web.dto.notification.NotificationResponse;
import lombok.RequiredArgsConstructor;
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

    // ✅ 특정 유저의 모든 알림 조회
    @GetMapping("/{userId}/all")
    public ResponseEntity<List<NotificationResponse>> getAllNotifications(@PathVariable Long userId) {
        List<NotificationResponse> notifications = notificationService.getAllNotifications(userId)
                .stream()
                .map(NotificationResponse::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(notifications);
    }

    // ✅ 특정 유저의 읽지 않은 알림 조회
    @GetMapping("/{userId}/unread")
    public ResponseEntity<List<NotificationResponse>> getUnreadNotifications(@PathVariable Long userId) {
        List<NotificationResponse> notifications = notificationService.getUnreadNotifications(userId)
                .stream()
                .map(NotificationResponse::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(notifications);
    }

    // ✅ 특정 알림 읽음 처리
    @PostMapping("/{notificationId}/read")
    public ResponseEntity<Void> markNotificationAsRead(@PathVariable Long notificationId) {
        notificationService.markAsRead(notificationId);
        return ResponseEntity.ok().build();
    }

    // ✅ 특정 유저의 모든 알림을 읽음 처리
    @PostMapping("/{userId}/mark-all-read")
    public ResponseEntity<Void> markAllNotificationsAsRead(@PathVariable Long userId) {
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
