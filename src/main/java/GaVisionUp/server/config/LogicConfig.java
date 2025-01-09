package GaVisionUp.server.config;

import GaVisionUp.server.repository.exp.expbar.ExpBarRepository;
import GaVisionUp.server.repository.exp.expbar.ExpBarRepositoryImpl;
import GaVisionUp.server.repository.exp.experience.ExperienceRepository;
import GaVisionUp.server.repository.exp.experience.ExperienceRepositoryImpl;
import GaVisionUp.server.repository.level.LevelRepository;
import GaVisionUp.server.repository.level.LevelRepositoryImpl;
import GaVisionUp.server.repository.user.UserRepository;
import GaVisionUp.server.service.exp.expbar.ExpBarService;
import GaVisionUp.server.service.exp.expbar.ExpBarServiceImpl;

import GaVisionUp.server.service.exp.experience.ExperienceService;
import GaVisionUp.server.service.exp.experience.ExperienceServiceImpl;
import GaVisionUp.server.service.level.LevelServiceImpl;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class LogicConfig {
    private final EntityManager em;
    private final UserRepository userRepository;

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
        return new ExperienceRepositoryImpl(em);
    }

    @Bean
    public ExperienceService experienceService() {
        return new ExperienceServiceImpl(experienceRepository(), userRepository, expBarRepository());
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
}
