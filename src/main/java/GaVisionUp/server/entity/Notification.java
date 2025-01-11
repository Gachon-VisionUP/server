package GaVisionUp.server.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Table(name = "notifications")
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false)
    private User user; // ✅ 알림 대상 유저

    @Column(nullable = false)
    private String title; // ✅ 알림 제목

    @Column(nullable = false)
    private String message; // ✅ 알림 내용

    @Builder.Default
    @Column(nullable = false)
    private boolean read = false; // ✅ 읽음 여부 (기본값: false)

    @Column(nullable = false)
    private LocalDateTime createdAt; // ✅ 알림 생성 시간

    public static Notification create(User user, String title, String message) {
        return Notification.builder()
                .user(user)
                .title(title)
                .message(message)
                .createdAt(LocalDateTime.now())
                .build();
    }

    // ✅ 알림 읽음 처리 메서드 추가
    public void markAsRead() {
        this.read = true;
    }
}
