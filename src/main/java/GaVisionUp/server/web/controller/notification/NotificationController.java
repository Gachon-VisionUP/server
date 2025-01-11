package GaVisionUp.server.web.controller.notification;

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

    // ✅ 특정 유저의 알림 목록 조회
    @GetMapping("/{userId}")
    public ResponseEntity<List<NotificationResponse>> getUserNotifications(@PathVariable Long userId) {
        List<NotificationResponse> notifications = notificationService.getUserNotifications(userId)
                .stream().map(NotificationResponse::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(notifications);
    }

    // ✅ 알림 읽음 처리
    @PatchMapping("/{notificationId}/read")
    public ResponseEntity<Void> markNotificationAsRead(@PathVariable Long notificationId) {
        notificationService.markAsRead(notificationId);
        return ResponseEntity.noContent().build();
    }
}
