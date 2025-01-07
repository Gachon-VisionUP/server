package GaVisionUp.server.repository.exp;

import GaVisionUp.server.entity.enums.Department;
import GaVisionUp.server.entity.exp.ExpBar;
import jakarta.persistence.EntityManager;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Slf4j
@SpringBootTest
@Transactional
class ExpBarRepositoryImplTest {

    @Autowired
    private ExpBarRepository expBarRepository;

    @Autowired
    private EntityManager em;

    private ExpBar testExpBar;

    @BeforeEach
    void setUp() {
        // Given: 테스트용 ExpBar 데이터 생성 및 저장 (필수 필드 포함)
        testExpBar = new ExpBar(1, Department.EUMSEONG1, "홍길동", "F1-Ⅰ", 500);
        expBarRepository.save(testExpBar);

        // 영속성 컨텍스트 반영
        em.flush();
        em.clear();
    }

    @Test
    void save_shouldPersistExpBar() {
        // Given
        ExpBar newExpBar = new ExpBar(2, Department.BUSINESS, "이몽룡", "F2-Ⅰ", 1000);

        // When
        ExpBar savedExpBar = expBarRepository.save(newExpBar);

        // Then
        Optional<ExpBar> foundExpBar = expBarRepository.findById(savedExpBar.getId());
        assertThat(foundExpBar).isPresent();
        assertThat(foundExpBar.get().getUserId()).isEqualTo(2);
        assertThat(foundExpBar.get().getTotalExp()).isEqualTo(1000);
        assertThat(foundExpBar.get().getDepartment()).isEqualTo(Department.BUSINESS);
        assertThat(foundExpBar.get().getName()).isEqualTo("이몽룡");
        assertThat(foundExpBar.get().getLevel()).isEqualTo("F2-Ⅰ");

        // 로그 출력
        log.info("Saved ExpBar: {}", foundExpBar.get());
    }

    @Test
    void findById_shouldReturnExpBar() {
        // When
        Optional<ExpBar> foundExpBar = expBarRepository.findById(testExpBar.getId());

        // Then
        assertTrue(foundExpBar.isPresent());
        assertThat(foundExpBar.get().getUserId()).isEqualTo(1);
        assertThat(foundExpBar.get().getTotalExp()).isEqualTo(500);
        assertThat(foundExpBar.get().getDepartment()).isEqualTo(Department.EUMSEONG1);
        assertThat(foundExpBar.get().getName()).isEqualTo("홍길동");
        assertThat(foundExpBar.get().getLevel()).isEqualTo("F1-Ⅰ");

        // 로그 출력
        log.info("Found ExpBar by ID: {}", foundExpBar.get());
    }

    @Test
    void findByUserId_shouldReturnCorrectExpBar() {
        // When
        Optional<ExpBar> foundExpBar = expBarRepository.findByUserId(1);

        // Then
        assertTrue(foundExpBar.isPresent());
        assertThat(foundExpBar.get().getTotalExp()).isEqualTo(500);
        assertThat(foundExpBar.get().getDepartment()).isEqualTo(Department.EUMSEONG1);
        assertThat(foundExpBar.get().getName()).isEqualTo("홍길동");
        assertThat(foundExpBar.get().getLevel()).isEqualTo("F1-Ⅰ");

        // 로그 출력
        log.info("Found ExpBar by User ID: {}", foundExpBar.get());
    }

    @Test
    void updateTotalExp_shouldIncreaseExp() {
        // Given
        int additionalExp = 200;

        // When
        expBarRepository.updateTotalExp(1, additionalExp);
        Optional<ExpBar> updatedExpBar = expBarRepository.findByUserId(1);

        // Then
        assertThat(updatedExpBar).isPresent();
        assertThat(updatedExpBar.get().getTotalExp()).isEqualTo(700); // 기존 500 + 200

        // 로그 출력
        log.info("Updated ExpBar (TotalExp Increased): {}", updatedExpBar.get());
    }
}
