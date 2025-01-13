package GaVisionUp.server.service.notification;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ExpoNotificationService {

    private static final String EXPO_PUSH_URL = "https://exp.host/--/api/v2/push/send";
    private final RestTemplate restTemplate = new RestTemplate();

    // ✅ Expo 서버로 푸쉬 알림 전송
    public void sendPushNotification(String pushToken, String title, String message) {
        if (pushToken == null || !pushToken.startsWith("ExponentPushToken")) {
            System.out.println("🚨 유효하지 않은 PushToken: " + pushToken);
            return;
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> body = Map.of(
                "to", pushToken,
                "title", title,
                "body", message
        );

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);
        ResponseEntity<String> response = restTemplate.postForEntity(EXPO_PUSH_URL, request, String.class);

        System.out.println("📨 Expo Push Notification Response: " + response.getBody());
    }

    /**
     * 모든 사용자에게 푸쉬 알림 전송
     */
    public void sendNotificationToAllUsers(List<String> expoPushTokens, String title, String body) {
        for (String token : expoPushTokens) {
            try {
                // 요청 헤더 설정
                HttpHeaders headers = new HttpHeaders();
                headers.add("Content-Type", "application/json");
                headers.add("Accept", "application/json");

                // 요청 본문 생성
                Map<String, Object> payload = new HashMap<>();
                payload.put("to", token);
                payload.put("title", title);
                payload.put("body", body);
                payload.put("sound", "default");

                // HTTP 요청 보내기
                HttpEntity<Map<String, Object>> request = new HttpEntity<>(payload, headers);
                ResponseEntity<String> response = restTemplate.postForEntity(EXPO_PUSH_URL, request, String.class);

                // 응답 처리 (성공/실패 로그)
                System.out.println("Response from Expo: " + response.getBody());
            } catch (Exception e) {
                System.err.println("Failed to send notification to token: " + token);
                e.printStackTrace();
            }
        }
    }
}
