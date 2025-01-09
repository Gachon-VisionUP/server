package GaVisionUp.server.repository.exp.expbar;

import GaVisionUp.server.entity.User;
import GaVisionUp.server.entity.enums.Department;
import GaVisionUp.server.entity.enums.ExpType;
import GaVisionUp.server.entity.enums.Role;
import GaVisionUp.server.entity.exp.ExpBar;
import GaVisionUp.server.entity.exp.Experience;
import GaVisionUp.server.repository.exp.experience.ExperienceRepository;
import jakarta.persistence.EntityManager;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
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
    private ExperienceRepository experienceRepository;

    @Autowired
    private EntityManager em;

    private User testUser;
    private ExpBar testExpBar;

    @BeforeEach
    void setUp() {
        // ✅ User 객체 생성
        testUser = User.builder()
                .employeeId("EMP0012") // 사번 추가
                .name("홍길동")
                .joinDate(LocalDate.of(2020, 5, 15))
                .department(Department.EUMSEONG1) // 소속 추가
                .part(1) // 직무 그룹
                .level("F1-Ⅰ") // 레벨
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
                .joinDate(LocalDate.of(2021, 7, 10))
                .department(Department.BUSINESS)
                .part(2)
                .level("F2-Ⅰ")
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

        ExpBar savedExpBar = expBarRepository.save(newExpBar);

        // ✅ ExpBar 검증
        Optional<ExpBar> foundExpBar = expBarRepository.findById(savedExpBar.getId());
        assertThat(foundExpBar).isPresent();
        assertThat(foundExpBar.get().getUser().getId()).isEqualTo(newUser.getId());
        assertThat(foundExpBar.get().getUser().getDepartment()).isEqualTo(Department.BUSINESS);
        assertThat(foundExpBar.get().getUser().getName()).isEqualTo("이몽룡");
        assertThat(foundExpBar.get().getUser().getLevel()).isEqualTo("F2-Ⅰ");

        log.info("✅ Saved ExpBar: {}", foundExpBar.get());
    }

    @Test
    void findById_shouldReturnExpBar() {
        // ✅ 기존 User ID를 기반으로 ExpBar 조회
        Optional<ExpBar> foundExpBar = expBarRepository.findById(testExpBar.getId());

        assertTrue(foundExpBar.isPresent());
        assertThat(foundExpBar.get().getUser().getId()).isEqualTo(testUser.getId());
        assertThat(foundExpBar.get().getUser().getDepartment()).isEqualTo(Department.EUMSEONG1);
        assertThat(foundExpBar.get().getUser().getName()).isEqualTo("홍길동");
        assertThat(foundExpBar.get().getUser().getLevel()).isEqualTo("F1-Ⅰ");

        log.info("✅ Found ExpBar by ID: {}", foundExpBar.get());
    }

    @Test
    void findByUserId_shouldReturnCorrectExpBar() {
        // ✅ 기존 User ID를 기반으로 ExpBar 조회
        Optional<ExpBar> foundExpBar = expBarRepository.findByUserId(testUser.getId());

        assertTrue(foundExpBar.isPresent());
        assertThat(foundExpBar.get().getUser().getDepartment()).isEqualTo(Department.EUMSEONG1);
        assertThat(foundExpBar.get().getUser().getName()).isEqualTo("홍길동");
        assertThat(foundExpBar.get().getUser().getLevel()).isEqualTo("F1-Ⅰ");

        log.info("✅ Found ExpBar by User ID: {}", foundExpBar.get());
    }

    @Test
    void experienceAddition_shouldUpdateExpBarTotalExp() {
        // ✅ 경험치 추가
        int expToAdd = 3000;
        Experience experience = new Experience(testUser, ExpType.H1_PERFORMANCE, expToAdd);
        experienceRepository.save(experience);
        em.flush();

        // ✅ ExpBar 조회 및 검증
        Optional<ExpBar> updatedExpBar = expBarRepository.findByUserId(testUser.getId());
        assertTrue(updatedExpBar.isPresent());
        assertThat(updatedExpBar.get().getUser().getTotalExp()).isEqualTo(expToAdd);

        log.info("✅ ExpBar totalExp updated after Experience addition: {}", updatedExpBar.get().getUser().getTotalExp());
    }
}
