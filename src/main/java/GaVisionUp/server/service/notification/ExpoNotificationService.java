package GaVisionUp.server.service.notification;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import java.util.Collections;
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
}
