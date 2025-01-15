package GaVisionUp.server.repository.exp.experience;

import GaVisionUp.server.entity.User;
import GaVisionUp.server.entity.enums.ExpType;
import GaVisionUp.server.entity.enums.JobGroup;
import GaVisionUp.server.entity.exp.ExpBar;
import GaVisionUp.server.entity.exp.Experience;
import GaVisionUp.server.entity.exp.QExperience;
import GaVisionUp.server.repository.exp.expbar.ExpBarRepository;
import GaVisionUp.server.repository.level.LevelRepository;
import GaVisionUp.server.service.notification.ExpoNotificationService;
import GaVisionUp.server.service.notification.NotificationService;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static GaVisionUp.server.entity.exp.QExperience.experience;

@Slf4j
@Repository
@Transactional
public class ExperienceRepositoryImpl implements ExperienceRepository {

    private final EntityManager em;
    private final JPAQueryFactory queryFactory;
    private final LevelRepository levelRepository; // ✅ 레벨 리포지토리 추가
    private final ExpBarRepository expBarRepository; // ✅ ExpBar 업데이트를 위해 추가
    private final NotificationService notificationService;
    private final ExpoNotificationService expoNotificationService;

    private final QExperience qExperience = experience;

    public ExperienceRepositoryImpl(EntityManager em, LevelRepository levelRepository, ExpBarRepository expBarRepository, NotificationService notificationService, ExpoNotificationService expoNotificationService) {
        this.em = em;
        this.queryFactory = new JPAQueryFactory(em);
        this.levelRepository = levelRepository;
        this.expBarRepository = expBarRepository;
        this.notificationService = notificationService;
        this.expoNotificationService = expoNotificationService;
    }

    @Override
    public Experience save(Experience experience) {
        // ✅ User 객체 검증
        User user = experience.getUser();
        if (user == null) {
            throw new IllegalArgumentException("Experience 저장 시 User 정보가 필요합니다.");
        }

        user.addExperience(experience.getExp()); // ✅ 경험치 추가
        upgradeUserLevel(user); // ✅ 레벨 업그레이드 검증

        em.merge(user); // ✅ User의 totalExp 변경 반영

        // ✅ ExpBar 업데이트 (레벨 변경 반영)
        updateExpBar(user);

        if (experience.getId() == null) {
            em.persist(experience);
        } else {
            em.merge(experience);
        }
        ExpType expType = experience.getExpType();
        int exp = experience.getExp();
        // ✅ 내부 알림 저장
        String title = "최신 경험치";
        String message = String.format("%d", exp);
        notificationService.createNotification(user, title, message, expType.getValue());

        // ✅ Expo 푸쉬 알림 전송
        expoNotificationService.sendPushNotification(user.getExpoPushToken(), title, message, expType.getValue());

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

    // ✅ 현재 경험치(totalExp)에 따라 레벨 자동 업그레이드
    private void upgradeUserLevel(User user) {
        JobGroup jobGroup = user.getLevel().getJobGroup(); // ✅ 현재 직군 가져오기
        levelRepository.findLevelByExp(jobGroup, user.getTotalExp()) // ✅ 총 경험치 기준으로 정확한 레벨 조회
                .ifPresent(user::setLevel); // ✅ 새로운 레벨 설정 (경험치 기준 충족 시)
    }

    // ✅ ExpBar 업데이트 (레벨 반영)
    private void updateExpBar(User user) {
        ExpBar expBar = expBarRepository.findByUserId(user.getId())
                .orElseThrow(() -> new IllegalArgumentException("해당 사원의 ExpBar가 존재하지 않습니다."));

        expBar.updateLevel(); // ✅ ExpBar의 levelName 업데이트
        em.merge(expBar); // ✅ 변경 사항 저장
    }

    @Override
    public Optional<Experience> findById(Long id) {
        return Optional.ofNullable(
                queryFactory
                        .selectFrom(qExperience)
                        .where(qExperience.id.eq(id))
                        .fetchOne()
        );
    }

    @Override
    public List<Experience> findByUserId(Long userId) {
        return queryFactory
                .selectFrom(qExperience)
                .where(qExperience.user.id.eq(userId))
                .fetch();
    }

    // ✅ 올해(Current Year) 경험치 조회 및 총 경험치 반영
    @Override
    public List<Experience> findByUserIdAndCurrentYear(Long userId, int currentYear, ExpBar expBar) {
        List<Experience> experiences = queryFactory
                .selectFrom(qExperience)
                .where(
                        qExperience.user.id.eq(userId)
                                .and(qExperience.obtainedDate.year().eq(currentYear)) // ✅ 현재 연도 필터링
                )
                .fetch();

        // ✅ currentTotalExp 업데이트
        int currentTotalExp = experiences.stream().mapToInt(Experience::getExp).sum();
        expBar.setCurrentTotalExp(currentTotalExp);
        em.merge(expBar); // 업데이트 적용

        return experiences;
    }

    // ✅ 작년(Previous Years)까지 경험치 조회 및 총 경험치 반영
    @Override
    public List<Experience> findByUserIdAndPreviousYears(Long userId, int previousYear, LocalDate joinDate, ExpBar expBar) {
        List<Experience> experiences = queryFactory
                .selectFrom(qExperience)
                .where(
                        qExperience.user.id.eq(userId)
                                .and(qExperience.obtainedDate.year().loe(previousYear)) // ✅ 작년까지 필터링
                                .and(qExperience.obtainedDate.goe(joinDate)) // ✅ 유저 입사일부터 필터링
                )
                .fetch();

        // ✅ previousTotalExp 업데이트
        int previousTotalExp = experiences.stream().mapToInt(Experience::getExp).sum();
        expBar.setPreviousTotalExp(previousTotalExp);
        em.merge(expBar); // 업데이트 적용

        return experiences;
    }

    // ✅ 최신 경험치 1개 조회 (ORDER BY obtainedDate DESC LIMIT 1)
    @Override
    public Optional<Experience> findTopByUserIdOrderByObtainedDateDesc(Long userId) {
        return Optional.ofNullable(
                queryFactory
                        .selectFrom(qExperience)
                        .where(qExperience.user.id.eq(userId))
                        .orderBy(qExperience.obtainedDate.desc()) // 최신 경험치 기준 정렬
                        .fetchFirst() // 가장 최신 경험치 1개 조회
        );
    }

    // ✅ 특정 연도의 최신 3개 경험치 조회
    @Override
    public List<Experience> findTop3ByUserIdAndYearOrderByObtainedDateDesc(Long userId, int year) {
        return queryFactory
                .selectFrom(qExperience)
                .where(
                        qExperience.user.id.eq(userId),
                        qExperience.obtainedDate.year().eq(year) // ✅ 특정 연도 필터링
                )
                .orderBy(qExperience.obtainedDate.desc()) // ✅ 최신순 정렬
                .limit(3) // ✅ 최신 3개 제한
                .fetch();
    }
}
