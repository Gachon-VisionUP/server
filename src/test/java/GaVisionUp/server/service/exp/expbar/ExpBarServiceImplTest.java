package GaVisionUp.server.service.exp.expbar;

import GaVisionUp.server.entity.User;
import GaVisionUp.server.entity.enums.Department;
import GaVisionUp.server.entity.enums.Role;
import GaVisionUp.server.entity.enums.ExpType;
import GaVisionUp.server.entity.exp.ExpBar;
import GaVisionUp.server.entity.exp.Experience;
import GaVisionUp.server.repository.exp.expbar.ExpBarRepository;
import GaVisionUp.server.repository.exp.experience.ExperienceRepository;
import GaVisionUp.server.repository.user.UserRepository;
import jakarta.persistence.EntityManager;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Slf4j
@SpringBootTest
@Transactional
class ExpBarServiceImplTest {

    @Autowired
    private ExpBarRepository expBarRepository;

    @Autowired
    private ExperienceRepository experienceRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EntityManager em;

    private User testUser;
    private ExpBar testExpBar;

    @BeforeEach
    void setUp() {
        // ✅ User 객체 생성
        testUser = User.builder()
                .employeeId("EMP0012")
                .name("홍길동")
                .joinDate(LocalDate.of(2020, 5, 15))
                .department(Department.EUMSEONG1)
                .part(1)
                .level(3)
                .loginId("hong")
                .password("test1234")
                .role(Role.USER)
                .profileImageUrl("https://example.com/profile.jpg")
                .totalExp(0) // 초기 경험치 0
                .build();

        userRepository.save(testUser);
        em.flush();

        // ✅ ExpBar 객체 저장 (User와 매핑)
        testExpBar = new ExpBar();
        testExpBar.setUser(testUser);
        testExpBar.setDepartment(testUser.getDepartment()); // User의 department 사용
        testExpBar.setName(testUser.getName());
        testExpBar.setLevel("F1-Ⅰ");

        expBarRepository.save(testExpBar);
        em.flush();
        em.clear();
    }

    @Test
    void createExpBar_shouldSaveExpBar() {
        // ✅ 새로운 User 객체 생성
        User newUser = User.builder()
                .employeeId("EMP0020")
                .name("이몽룡")
                .joinDate(LocalDate.of(2021, 7, 10))
                .department(Department.BUSINESS)
                .part(2)
                .level(2)
                .loginId("mongryong")
                .password("test1234")
                .role(Role.USER)
                .profileImageUrl("https://example.com/profile2.jpg")
                .totalExp(0)
                .build();

        userRepository.save(newUser);
        em.flush();

        // ✅ ExpBar 저장
        ExpBar newExpBar = new ExpBar();
        newExpBar.setUser(newUser);
        newExpBar.setDepartment(newUser.getDepartment());
        newExpBar.setName(newUser.getName());
        newExpBar.setLevel("F2-Ⅰ");

        ExpBar savedExpBar = expBarRepository.save(newExpBar);

        // ✅ ExpBar 검증
        assertThat(savedExpBar).isNotNull();
        assertThat(savedExpBar.getUser().getId()).isEqualTo(newUser.getId());

        log.info("✅ Created ExpBar: {}", savedExpBar);
    }

    @Test
    void getExpBarByUserId_shouldReturnExpBar() {
        // ✅ 기존 User ID를 기반으로 ExpBar 조회
        ExpBar foundExpBar = expBarRepository.findByUserId(testUser.getId())
                .orElseThrow(() -> new IllegalArgumentException("해당 사원의 경험치 바가 존재하지 않습니다."));

        // ✅ ExpBar 검증
        assertThat(foundExpBar).isNotNull();
        assertThat(foundExpBar.getUser().getId()).isEqualTo(testUser.getId());

        log.info("✅ Found ExpBar: {}", foundExpBar);
    }

    @Test
    void getExpBarByUserId_shouldThrowExceptionIfNotFound() {
        // ✅ 존재하지 않는 User ID로 조회하여 예외 발생 확인
        Exception exception = assertThrows(IllegalArgumentException.class, () ->
                expBarRepository.findByUserId(999L)
                        .orElseThrow(() -> new IllegalArgumentException("해당 사원의 경험치 바가 존재하지 않습니다.")));

        assertThat(exception.getMessage()).isEqualTo("해당 사원의 경험치 바가 존재하지 않습니다.");

        log.info("✅ Exception occurred: {}", exception.getMessage());
    }

    @Test
    void experienceAddition_shouldUpdateExpBarTotalExp() {
        // ✅ 경험치 추가
        int expToAdd = 3000;
        Experience experience = new Experience(testUser, ExpType.인사평가, expToAdd);
        experienceRepository.save(experience);
        em.flush();

        // ✅ ExpBar 조회 및 검증
        ExpBar updatedExpBar = expBarRepository.findByUserId(testUser.getId())
                .orElseThrow(() -> new IllegalArgumentException("해당 사원의 경험치 바가 존재하지 않습니다."));
        assertThat(updatedExpBar.getUser().getTotalExp()).isEqualTo(expToAdd);

        log.info("✅ ExpBar totalExp updated after Experience addition: {}", updatedExpBar.getUser().getTotalExp());
    }
}
