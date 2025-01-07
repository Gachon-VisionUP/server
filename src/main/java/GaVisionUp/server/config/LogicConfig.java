package GaVisionUp.server.config;

import GaVisionUp.server.repository.exp.expbar.ExpBarRepository;
import GaVisionUp.server.repository.exp.expbar.ExpBarRepositoryImpl;
import GaVisionUp.server.service.exp.expbar.ExpBarService;
import GaVisionUp.server.service.exp.expbar.ExpBarServiceImpl;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class LogicConfig {
    private final EntityManager em;

    // 실제 사용할 리포지토리
    @Bean
    public ExpBarRepository expBarRepository() {
        return new ExpBarRepositoryImpl(em);
    }

    /**
     *     인 메모리 테스트용 리포지토리
    //@Bean
    public ExpBarRepository expBarRepository() {
        return new ExpBarMemoryRepository();
    }
     */

    @Bean
    public ExpBarService expBarService() {
        return new ExpBarServiceImpl(expBarRepository());
    }
}
