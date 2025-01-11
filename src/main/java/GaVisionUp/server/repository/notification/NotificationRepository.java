package GaVisionUp.server.repository.notification;

import GaVisionUp.server.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    // ✅ 특정 유저의 모든 알림 조회 (내림차순)
    List<Notification> findByUserIdOrderByCreatedAtDesc(Long userId);

    // ✅ 특정 유저의 안 읽은 알림 조회 (내림차순)
    List<Notification> findByUserIdAndReadFalseOrderByCreatedAtDesc(Long userId);

}
