package GaVisionUp.server.web.controller.notification;

import GaVisionUp.server.entity.Notification;
import GaVisionUp.server.service.notification.NotificationService;
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
}
