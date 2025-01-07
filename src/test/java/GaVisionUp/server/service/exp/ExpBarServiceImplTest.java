package GaVisionUp.server.service.exp;

import GaVisionUp.server.entity.enums.Department;
import GaVisionUp.server.entity.exp.ExpBar;
import GaVisionUp.server.repository.exp.ExpBarRepository;
import jakarta.persistence.EntityManager;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Slf4j
@SpringBootTest
@Transactional
class ExpBarServiceImplTest {

    @Autowired
    private ExpBarService expBarService;

    @Autowired
    private ExpBarRepository expBarRepository;

    @Autowired
    private EntityManager em;

    private ExpBar testExpBar;

    @BeforeEach
    void setUp() {
        // Given: 테스트용 ExpBar 데이터 생성 및 저장
        testExpBar = new ExpBar(1, Department.음성1센터, "홍길동", "F1-Ⅰ", 500);
        expBarRepository.save(testExpBar);

        // 영속성 컨텍스트 반영
        em.flush();
        em.clear();
    }

    @Test
    void createExpBar_shouldSaveExpBar() {
        // Given
        ExpBar newExpBar = new ExpBar(2, Department.사업기획팀, "이몽룡", "F2-Ⅰ", 1000);

        // When
        ExpBar savedExpBar = expBarService.createExpBar(newExpBar);

        // Then
        assertThat(savedExpBar).isNotNull();
        assertThat(savedExpBar.getUserId()).isEqualTo(2);
        assertThat(savedExpBar.getTotalExp()).isEqualTo(1000);

        // 로그 출력
        log.info("Created ExpBar: {}", savedExpBar);
    }

    @Test
    void getExpBarByUserId_shouldReturnExpBar() {
        // When
        ExpBar foundExpBar = expBarService.getExpBarByUserId(1);

        // Then
        assertThat(foundExpBar).isNotNull();
        assertThat(foundExpBar.getUserId()).isEqualTo(1);
        assertThat(foundExpBar.getTotalExp()).isEqualTo(500);

        // 로그 출력
        log.info("Found ExpBar: {}", foundExpBar);
    }

    @Test
    void getExpBarByUserId_shouldThrowExceptionIfNotFound() {
        // When & Then
        Exception exception = assertThrows(IllegalArgumentException.class, () ->
                expBarService.getExpBarByUserId(99)); // 존재하지 않는 ID

        assertThat(exception.getMessage()).isEqualTo("해당 사원의 경험치 바가 존재하지 않습니다.");

        // 로그 출력
        log.info("Exception occurred: {}", exception.getMessage());
    }

    @Test
    void addExperience_shouldIncreaseTotalExp() {
        // Given
        int additionalExp = 200;

        // When
        ExpBar updatedExpBar = expBarService.addExperience(1, additionalExp);

        // Then
        assertThat(updatedExpBar).isNotNull();
        assertThat(updatedExpBar.getTotalExp()).isEqualTo(700); // 기존 500 + 200

        // 로그 출력
        log.info("Updated ExpBar (TotalExp Increased): {}", updatedExpBar);
    }
}
