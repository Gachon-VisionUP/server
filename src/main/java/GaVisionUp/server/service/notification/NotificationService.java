package GaVisionUp.server.service.notification;

import GaVisionUp.server.entity.User;
import GaVisionUp.server.entity.Notification;
import GaVisionUp.server.repository.notification.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
    /* Expo 토큰 필요
    private final RestTemplate restTemplate = new RestTemplate(); // ✅ REST API 호출용
    private static final String EXPO_PUSH_URL = "https://exp.host/--/api/v2/push/send";
     */

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

    /* Expo 토큰 필요
    // ✅ 알림 생성 및 Expo 푸쉬 알림 전송
    public Notification createNotification(User user, String title, String message) {
        Notification notification = Notification.builder()
                .user(user)
                .title(title)
                .message(message)
                .createdAt(LocalDateTime.now())
                .build();
        notificationRepository.save(notification);

        // ✅ Expo 푸쉬 토큰이 있는 경우에만 푸쉬 전송
        if (user.getExpoPushToken() != null) {
            sendPushNotification(user.getExpoPushToken(), title, message);
        }

        return notification;
    }

    // ✅ Expo 푸쉬 알림 전송 메서드 추가
    private void sendPushNotification(String pushToken, String title, String message) {
        if (!pushToken.startsWith("ExponentPushToken")) {
            throw new IllegalArgumentException("올바르지 않은 Expo 푸쉬 토큰입니다.");
        }

        // ✅ 푸쉬 알림 요청 데이터 생성
        Map<String, Object> pushData = new HashMap<>();
        pushData.put("to", pushToken);
        pushData.put("title", title);
        pushData.put("body", message);
        pushData.put("sound", "default"); // ✅ 기본 알림음 설정

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(pushData, headers);

        // ✅ Expo 푸쉬 API 호출
        ResponseEntity<String> response = restTemplate.postForEntity(EXPO_PUSH_URL, request, String.class);

        if (!response.getStatusCode().is2xxSuccessful()) {
            throw new RuntimeException("Expo 푸쉬 알림 전송 실패: " + response.getBody());
        }
    }
     */

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
