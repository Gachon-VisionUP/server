package GaVisionUp.server.service.exp.experience;

import GaVisionUp.server.entity.User;
import GaVisionUp.server.entity.enums.ExpType;
import GaVisionUp.server.entity.exp.ExpBar;
import GaVisionUp.server.entity.exp.Experience;
import GaVisionUp.server.repository.exp.expbar.ExpBarRepository;
import GaVisionUp.server.repository.exp.experience.ExperienceRepository;
import GaVisionUp.server.repository.user.UserRepository;
import GaVisionUp.server.service.notification.ExpoNotificationService;
import GaVisionUp.server.service.notification.NotificationService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class ExperienceServiceImpl implements ExperienceService {

    private final ExperienceRepository experienceRepository;
    private final UserRepository userRepository;
    private final ExpBarRepository expBarRepository;
    private final NotificationService notificationService;
    private final ExpoNotificationService expoNotificationService;

    // ✅ 경험치 추가 및 알림 전송
    public Experience addExperience(Long userId, ExpType expType, int exp) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사원입니다."));

        Experience experience = new Experience(user, expType, exp);
        experienceRepository.save(experience);

        // ✅ 내부 알림 저장
        String title = "📢 경험치 획득!";
        String message = String.format("%s님, %s 경험치 %d점을 획득했습니다!", user.getName(), expType.name(), exp);
        notificationService.createNotification(user, title, message);

        // ✅ Expo 푸쉬 알림 전송
        expoNotificationService.sendPushNotification(user.getExpoPushToken(), title, message);

        log.info("✅ 경험치 추가 및 알림 전송 완료 - 유저: {}, ExpType: {}, 획득 경험치: {}", user.getName(), expType, exp);
        return experience;
    }

    /* 추후에 expo 토큰 추가되면 사용
    public Experience addExperience(Long userId, ExpType expType, int exp) {
    User user = userRepository.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사원입니다."));

    Experience experience = new Experience(user, expType, exp);
    experienceRepository.save(experience);

    // ✅ 푸쉬 알림 생성 및 전송 (NotificationService에서 처리)
    String title = "📢 경험치 획득!";
    String message = String.format("%s님, %s 경험치 %d점을 획득했습니다!", user.getName(), expType.name(), exp);
    notificationService.createNotification(user, title, message);

    log.info("✅ 경험치 추가 및 푸쉬 알림 전송 완료 - 유저: {}, ExpType: {}, 획득 경험치: {}", user.getName(), expType, exp);
    return experience;
}
     */

    // ✅ 특정 경험치의 경험치 내역 조회
    @Override
    public Optional<Experience> getExperienceById(Long id) {
        return experienceRepository.findById(id)
                .or(() -> {
                    throw new IllegalArgumentException("해당 경험치 기록이 존재하지 않습니다.");
                });
    }

    // ✅ 특정 유저의 경험치 내역 조회
    @Override
    public List<Experience> getExperiencesByUserId(Long userId) {
        return experienceRepository.findByUserId(userId);
    }

    // ✅ 올해(Current Year) 경험치 조회
    @Override
    public List<Experience> getExperiencesByCurrentYear(Long userId, int currentYear) {
        ExpBar expBar = expBarRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("ExpBar를 찾을 수 없습니다."));
        return experienceRepository.findByUserIdAndCurrentYear(userId, currentYear, expBar);
    }

    // ✅ 특정 유저의 작년까지 경험치 조회
    @Override
    public List<Experience> getExperiencesByPreviousYears(Long userId, int previousYear) {
        ExpBar expBar = expBarRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("ExpBar를 찾을 수 없습니다."));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사원입니다."));
        return experienceRepository.findByUserIdAndPreviousYears(userId, previousYear, user.getJoinDate(), expBar);
    }
}
