package GaVisionUp.server.service.notification;

import GaVisionUp.server.entity.User;
import GaVisionUp.server.entity.Notification;
import GaVisionUp.server.repository.notification.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;

    // ✅ 특정 유저의 읽지 않은 알림 목록 조회
    public List<Notification> getUnreadNotifications(Long userId) {
        return notificationRepository.findByUserIdAndReadFalseOrderByCreatedAtDesc(userId);
    }

    // ✅ 특정 유저의 모든 알림 조회 (읽음/안 읽음 관계없이)
    public List<Notification> getAllNotifications(Long userId) {
        return notificationRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }

    // ✅ 알림 생성 및 저장
    public Notification createNotification(User user, String title, String message) {
        Notification notification = Notification.builder()
                .user(user)
                .title(title)
                .message(message)
                .createdAt(LocalDateTime.now())
                .build();
        return notificationRepository.save(notification);
    }

    // ✅ 알림 읽음 처리
    public void markAsRead(Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new IllegalArgumentException("알림을 찾을 수 없습니다."));
        notification = Notification.builder()
                .id(notification.getId())
                .user(notification.getUser())
                .title(notification.getTitle())
                .message(notification.getMessage())
                .read(true) // ✅ 읽음 처리
                .createdAt(notification.getCreatedAt())
                .build();
        notificationRepository.save(notification);
    }

    // ✅ 특정 유저의 모든 알림을 읽음 처리
    public void markAllAsRead(Long userId) {
        List<Notification> notifications = notificationRepository.findByUserIdAndReadFalseOrderByCreatedAtDesc(userId);

        if (notifications.isEmpty()) {
            return; // ✅ 읽지 않은 알림이 없으면 아무것도 하지 않음
        }

        notifications.forEach(notification -> {
            notification.setRead(true); // ✅ 읽음 상태 변경
        });

        notificationRepository.saveAll(notifications); // ✅ 일괄 저장
    }

}
