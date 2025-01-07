package GaVisionUp.server.config;

import GaVisionUp.server.repository.exp.expbar.ExpBarRepository;
import GaVisionUp.server.repository.exp.expbar.ExpBarRepositoryImpl;
import GaVisionUp.server.repository.exp.personalexp.PersonalExpRepository;
import GaVisionUp.server.repository.exp.personalexp.PersonalExpRepositoryImpl;
import GaVisionUp.server.service.exp.expbar.ExpBarService;
import GaVisionUp.server.service.exp.expbar.ExpBarServiceImpl;

import GaVisionUp.server.service.exp.personalexp.PersonalExpService;
import GaVisionUp.server.service.exp.personalexp.PersonalExpServiceImpl;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class LogicConfig {
    private final EntityManager em;

    /**
     *     경험치 바
      */
    // 실제 사용할 리포지토리
    @Bean
    public ExpBarRepository expBarRepository() {
        return new ExpBarRepositoryImpl(em);
    }
    @Bean
    public ExpBarService expBarService() {
        return new ExpBarServiceImpl(expBarRepository());
    }
    /*
    public ExpBarRepository expBarRepository() {
        return new ExpBarMemoryRepository();
    }
     */


    /**
     *     개별 경험치
     */
    @Bean
    public PersonalExpRepository personalExpRepository() {
        return new PersonalExpRepositoryImpl(em);
    }

    @Bean
    public PersonalExpService personalExpService() {
        return new PersonalExpServiceImpl(personalExpRepository(), expBarRepository());
    }
}
