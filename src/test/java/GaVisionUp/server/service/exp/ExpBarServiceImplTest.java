package GaVisionUp.server.service.exp;

import GaVisionUp.server.entity.User;
import GaVisionUp.server.entity.enums.Department;
import GaVisionUp.server.entity.exp.ExpBar;
import GaVisionUp.server.repository.exp.expbar.ExpBarRepository;
import GaVisionUp.server.service.exp.expbar.ExpBarService;
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

    private User testUser;
    private ExpBar testExpBar;

    @BeforeEach
    void setUp() {
        // ✅ 기존 User ID를 고정하여 생성
        testUser = new User();
        testUser.setName("홍길동");
        testUser.setLoginId("hong");
        testUser.setPassword("test1234");
        testUser.setTotalExp(0);

        em.persist(testUser);  // User 저장
        em.flush();  // ★ DB 반영

        // ✅ ExpBar 데이터 생성 및 저장
        testExpBar = new ExpBar(testUser, Department.음성1센터, "홍길동", "F1-Ⅰ", 500);
        expBarRepository.save(testExpBar);

        em.flush();  // ★ DB 반영
        em.clear();  // ★ 영속성 컨텍스트 초기화
    }

    @Test
    void createExpBar_shouldSaveExpBar() {
        // ✅ 새로운 User 생성
        User newUser = new User();
        newUser.setName("이몽룡");
        newUser.setLoginId("mongryong");
        newUser.setPassword("test1234");
        newUser.setTotalExp(0);

        em.persist(newUser);
        em.flush();

        // ✅ ExpBar 저장
        ExpBar newExpBar = new ExpBar(newUser, Department.사업기획팀, "이몽룡", "F2-Ⅰ", 1000);
        ExpBar savedExpBar = expBarService.createExpBar(newExpBar);

        // ✅ ExpBar 검증
        assertThat(savedExpBar).isNotNull();
        assertThat(savedExpBar.getUser().getId()).isEqualTo(newUser.getId());
        assertThat(savedExpBar.getTotalExp()).isEqualTo(1000);

        log.info("✅ Created ExpBar: {}", savedExpBar);
    }

    @Test
    void getExpBarByUserId_shouldReturnExpBar() {
        // ✅ 기존 User ID를 기반으로 ExpBar 조회
        ExpBar foundExpBar = expBarService.getExpBarByUserId(testUser.getId());

        // ✅ ExpBar 검증
        assertThat(foundExpBar).isNotNull();
        assertThat(foundExpBar.getUser().getId()).isEqualTo(testUser.getId());
        assertThat(foundExpBar.getTotalExp()).isEqualTo(500);

        log.info("✅ Found ExpBar: {}", foundExpBar);
    }

    @Test
    void getExpBarByUserId_shouldThrowExceptionIfNotFound() {
        // ✅ 존재하지 않는 User ID로 조회하여 예외 발생 확인
        Exception exception = assertThrows(IllegalArgumentException.class, () ->
                expBarService.getExpBarByUserId(999L)); // 존재하지 않는 ID

        assertThat(exception.getMessage()).isEqualTo("해당 사원의 경험치 바가 존재하지 않습니다.");

        log.info("✅ Exception occurred: {}", exception.getMessage());
    }

    @Test
    void addExperience_shouldIncreaseTotalExp() {
        // ✅ 기존 ExpBar에 경험치 추가
        int additionalExp = 200;
        ExpBar updatedExpBar = expBarService.addExperience(testUser.getId(), additionalExp);

        // ✅ ExpBar 검증
        assertThat(updatedExpBar).isNotNull();
        assertThat(updatedExpBar.getTotalExp()).isEqualTo(700); // 기존 500 + 200

        log.info("✅ Updated ExpBar (TotalExp Increased): {}", updatedExpBar);
    }
}
