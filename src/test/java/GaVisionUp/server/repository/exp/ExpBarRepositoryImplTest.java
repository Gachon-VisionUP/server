package GaVisionUp.server.repository.exp;

import GaVisionUp.server.entity.User;
import GaVisionUp.server.entity.enums.Department;
import GaVisionUp.server.entity.enums.Role;
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
        // ✅ User 객체 생성 (Builder 사용)
        testUser = User.builder()
                .employeeId("EMP0012") // 사번 추가
                .name("홍길동")
                .joinDate(java.time.LocalDate.of(2020, 5, 15))
                .department(Department.음성1센터) // 소속 추가
                .part(1) // 직무 그룹
                .level(3) // 레벨
                .loginId("hong")
                .password("test1234")
                .role(Role.USER) // 역할 추가
                .profileImageUrl("https://example.com/profile.jpg") // 프로필 이미지 추가
                .totalExp(0)
                .build();

        em.persist(testUser);
        em.flush();

        // ✅ ExpBar 객체 저장 (User와 매핑)
        testExpBar = new ExpBar();
        testExpBar.setUser(testUser);
        testExpBar.setDepartment(testUser.getDepartment()); // User의 department 사용
        testExpBar.setName(testUser.getName());
        testExpBar.setLevel("F1-Ⅰ");
        testExpBar.setTotalExp(500);

        expBarRepository.save(testExpBar);
        em.flush();
        em.clear();
    }

    @Test
    void save_shouldPersistExpBar() {
        // ✅ 새로운 User 객체 저장
        User newUser = User.builder()
                .employeeId("EMP0020")
                .name("이몽룡")
                .joinDate(java.time.LocalDate.of(2021, 7, 10))
                .department(Department.사업기획팀)
                .part(2)
                .level(2)
                .loginId("mongryong")
                .password("test1234")
                .role(Role.USER)
                .profileImageUrl("https://example.com/profile2.jpg")
                .totalExp(0)
                .build();

        em.persist(newUser);
        em.flush();

        // ✅ ExpBar 저장
        ExpBar newExpBar = new ExpBar();
        newExpBar.setUser(newUser);
        newExpBar.setDepartment(newUser.getDepartment());
        newExpBar.setName(newUser.getName());
        newExpBar.setLevel("F2-Ⅰ");
        newExpBar.setTotalExp(1000);

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
        // ✅ ExpBar 경험치 증가
        int additionalExp = 200;
        expBarRepository.updateTotalExp(testUser.getId(), additionalExp);

        // ✅ ExpBar 조회
        Optional<ExpBar> updatedExpBar = expBarRepository.findByUserId(testUser.getId());

        assertThat(updatedExpBar).isPresent();
        assertThat(updatedExpBar.get().getTotalExp()).isEqualTo(700); // 기존 500 + 200

        log.info("✅ Updated ExpBar (TotalExp Increased): {}", updatedExpBar.get());
    }
}
