package GaVisionUp.server.config;

import GaVisionUp.server.repository.exp.ExpBarMemoryRepository;
import GaVisionUp.server.repository.exp.ExpBarRepository;
import GaVisionUp.server.repository.exp.ExpBarRepositoryImpl;
import GaVisionUp.server.service.exp.ExpBarService;
import GaVisionUp.server.service.exp.ExpBarServiceImpl;

import com.querydsl.jpa.impl.JPAQueryFactory;
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
