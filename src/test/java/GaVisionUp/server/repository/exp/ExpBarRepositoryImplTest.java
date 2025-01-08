package GaVisionUp.server.repository.exp;

import GaVisionUp.server.entity.User;
import GaVisionUp.server.entity.enums.Department;
import GaVisionUp.server.entity.exp.ExpBar;
import GaVisionUp.server.repository.exp.expbar.ExpBarRepository;
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

    private User testUser;
    private ExpBar testExpBar;

    @BeforeEach
    void setUp() {
        // ✅ 기존 User ID를 1로 고정하여 생성
        testUser = new User();
        testUser.setName("홍길동");
        testUser.setLoginId("hong");
        testUser.setPassword("test1234");
        testUser.setTotalExp(0);

        em.persist(testUser); // User를 먼저 저장
        em.flush(); // DB 반영

        // ✅ ExpBar 저장 (User와 매핑)
        testExpBar = new ExpBar(testUser, Department.음성1센터, "홍길동", "F1-Ⅰ", 500);
        expBarRepository.save(testExpBar);

        em.flush();
        em.clear();
    }

    @Test
    void save_shouldPersistExpBar() {
        // ✅ 새로운 Users 엔티티 먼저 저장
        User newUser = new User();
        newUser.setName("이몽룡");
        newUser.setLoginId("mongryong");
        newUser.setPassword("test1234");
        newUser.setTotalExp(0);

        em.persist(newUser);
        em.flush();

        // ✅ ExpBar 저장
        ExpBar newExpBar = new ExpBar(newUser, Department.사업기획팀, "이몽룡", "F2-Ⅰ", 1000);
        ExpBar savedExpBar = expBarRepository.save(newExpBar);

        // ✅ ExpBar 검증
        Optional<ExpBar> foundExpBar = expBarRepository.findById(savedExpBar.getId());
        assertThat(foundExpBar).isPresent();
        assertThat(foundExpBar.get().getUser().getId()).isEqualTo(newUser.getId());
        assertThat(foundExpBar.get().getTotalExp()).isEqualTo(1000);
        assertThat(foundExpBar.get().getDepartment()).isEqualTo(Department.사업기획팀);
        assertThat(foundExpBar.get().getName()).isEqualTo("이몽룡");
        assertThat(foundExpBar.get().getLevel()).isEqualTo("F2-Ⅰ");

        log.info("✅ Saved ExpBar: {}", foundExpBar.get());
    }

    @Test
    void findById_shouldReturnExpBar() {
        // ✅ 기존 User ID를 기반으로 ExpBar 조회
        Optional<ExpBar> foundExpBar = expBarRepository.findById(testExpBar.getId());

        assertTrue(foundExpBar.isPresent());
        assertThat(foundExpBar.get().getUser().getId()).isEqualTo(testUser.getId());
        assertThat(foundExpBar.get().getTotalExp()).isEqualTo(500);
        assertThat(foundExpBar.get().getDepartment()).isEqualTo(Department.음성1센터);
        assertThat(foundExpBar.get().getName()).isEqualTo("홍길동");
        assertThat(foundExpBar.get().getLevel()).isEqualTo("F1-Ⅰ");

        log.info("✅ Found ExpBar by ID: {}", foundExpBar.get());
    }

    @Test
    void findByUserId_shouldReturnCorrectExpBar() {
        // ✅ 기존 User ID를 기반으로 ExpBar 조회
        Optional<ExpBar> foundExpBar = expBarRepository.findByUserId(testUser.getId());

        assertTrue(foundExpBar.isPresent());
        assertThat(foundExpBar.get().getTotalExp()).isEqualTo(500);
        assertThat(foundExpBar.get().getDepartment()).isEqualTo(Department.음성1센터);
        assertThat(foundExpBar.get().getName()).isEqualTo("홍길동");
        assertThat(foundExpBar.get().getLevel()).isEqualTo("F1-Ⅰ");

        log.info("✅ Found ExpBar by User ID: {}", foundExpBar.get());
    }

    @Test
    void updateTotalExp_shouldIncreaseExp() {
        // ✅ ExpBar 업데이트
        int additionalExp = 200;
        expBarRepository.updateTotalExp(testUser.getId(), additionalExp);

        // ✅ ExpBar 조회
        Optional<ExpBar> updatedExpBar = expBarRepository.findByUserId(testUser.getId());

        assertThat(updatedExpBar).isPresent();
        assertThat(updatedExpBar.get().getTotalExp()).isEqualTo(700); // 기존 500 + 200

        log.info("✅ Updated ExpBar (TotalExp Increased): {}", updatedExpBar.get());
    }
}
