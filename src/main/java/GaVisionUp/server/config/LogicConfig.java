package GaVisionUp.server.config;

import GaVisionUp.server.repository.exp.expbar.ExpBarRepository;
import GaVisionUp.server.repository.exp.expbar.ExpBarRepositoryImpl;
import GaVisionUp.server.repository.exp.experience.ExperienceRepository;
import GaVisionUp.server.repository.exp.experience.ExperienceRepositoryImpl;
import GaVisionUp.server.repository.quest.job.JobQuestRepository;
import GaVisionUp.server.repository.quest.job.JobQuestRepositoryImpl;
import GaVisionUp.server.repository.quest.job.detail.JobQuestDetailRepository;
import GaVisionUp.server.repository.quest.job.detail.JobQuestDetailRepositoryImpl;
import GaVisionUp.server.repository.level.LevelRepository;
import GaVisionUp.server.repository.level.LevelRepositoryImpl;
import GaVisionUp.server.repository.performance.PerformanceReviewRepository;
import GaVisionUp.server.repository.performance.PerformanceReviewRepositoryImpl;
import GaVisionUp.server.repository.quest.leader.LeaderQuestRepository;
import GaVisionUp.server.repository.quest.leader.LeaderQuestRepositoryImpl;
import GaVisionUp.server.repository.quest.leader.condition.LeaderQuestConditionRepository;
import GaVisionUp.server.repository.quest.leader.condition.LeaderQuestConditionRepositoryImpl;
import GaVisionUp.server.repository.user.UserRepository;
import GaVisionUp.server.service.exp.expbar.ExpBarService;
import GaVisionUp.server.service.exp.expbar.ExpBarServiceImpl;

import GaVisionUp.server.service.exp.experience.ExperienceService;
import GaVisionUp.server.service.exp.experience.ExperienceServiceImpl;
import GaVisionUp.server.service.notification.ExpoNotificationService;
import GaVisionUp.server.service.notification.NotificationService;
import GaVisionUp.server.service.quest.job.JobQuestService;
import GaVisionUp.server.service.quest.job.JobQuestServiceImpl;
import GaVisionUp.server.service.level.LevelServiceImpl;
import GaVisionUp.server.service.performance.PerformanceReviewService;
import GaVisionUp.server.service.performance.PerformanceReviewServiceImpl;
import GaVisionUp.server.service.quest.job.detail.JobQuestDetailService;
import GaVisionUp.server.service.quest.job.detail.JobQuestDetailServiceImpl;
import GaVisionUp.server.service.quest.leader.LeaderQuestService;
import GaVisionUp.server.service.quest.leader.LeaderQuestServiceImpl;
import GaVisionUp.server.service.quest.leader.condition.LeaderQuestConditionService;
import GaVisionUp.server.service.quest.leader.condition.LeaderQuestConditionServiceImpl;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class LogicConfig {
    private final EntityManager em;
    private final UserRepository userRepository;
    private final NotificationService notificationService;
    private final ExpoNotificationService expoNotificationService;

    @Bean
    public JPAQueryFactory jpaQueryFactory() {
        return new JPAQueryFactory(em);
    }

    // 경험치 바
    @Bean
    public ExpBarRepository expBarRepository() {
        return new ExpBarRepositoryImpl(em);
    }
    @Bean
    public ExpBarService expBarService(UserRepository userRepository) {
        return new ExpBarServiceImpl(expBarRepository(), userRepository);
    }


    // 경험치 목록
    @Bean
    public ExperienceRepository experienceRepository() {
        return new ExperienceRepositoryImpl(em,levelRepository(), expBarRepository());
    }

    @Bean
    public ExperienceService experienceService() {
        return new ExperienceServiceImpl(experienceRepository(), userRepository, expBarRepository(), notificationService, expoNotificationService);
    }

    // 레벨 빈 등록
    @Bean
    public LevelRepository levelRepository(){
        return new LevelRepositoryImpl(em);
    }
    @Bean
    public LevelServiceImpl levelService(){
        return new LevelServiceImpl(levelRepository());
    }

    // 인사평가 빈 등록
    @Bean
    public PerformanceReviewRepository performanceReviewRepository(){
        return new PerformanceReviewRepositoryImpl(em);
    }

    @Bean
    public PerformanceReviewService performanceReviewService(){
        return new PerformanceReviewServiceImpl(userRepository, experienceRepository(),performanceReviewRepository());
    }

    // 직무별 퀘스트
    @Bean
    public JobQuestRepository jobQuestRepository(){
        return new JobQuestRepositoryImpl(em);
    }

    @Bean
    public JobQuestService jobQuestService(){
        return new JobQuestServiceImpl(jobQuestRepository(), userRepository, experienceRepository(), jobQuestDetailRepository());
    }

    @Bean
    public JobQuestDetailRepository jobQuestDetailRepository(){
        return new JobQuestDetailRepositoryImpl(em);
    }

    @Bean
    public JobQuestDetailService jobQuestDetailService(){
        return new JobQuestDetailServiceImpl(jobQuestDetailRepository());
    }

    // 리더 부여 퀘스트
    @Bean
    public LeaderQuestRepository leaderQuestRepository(){
        return new LeaderQuestRepositoryImpl(em);
    }

    @Bean
    public LeaderQuestService leaderQuestService(){
        return new LeaderQuestServiceImpl(leaderQuestRepository(), leaderQuestConditionRepository(), userRepository, experienceRepository());
    }

    @Bean
    public LeaderQuestConditionRepository leaderQuestConditionRepository(){
        return new LeaderQuestConditionRepositoryImpl(em);
    }

    @Bean
    public LeaderQuestConditionService leaderQuestConditionService(){
        return new LeaderQuestConditionServiceImpl(leaderQuestConditionRepository());
    }
}
