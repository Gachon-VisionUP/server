package GaVisionUp.server.repository.exp.experience;

import GaVisionUp.server.entity.User;
import GaVisionUp.server.entity.enums.Department;
import GaVisionUp.server.entity.enums.ExpType;
import GaVisionUp.server.entity.enums.Role;
import GaVisionUp.server.entity.exp.Experience;
import GaVisionUp.server.repository.user.UserRepository;
import jakarta.persistence.EntityManager;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
@DataJpaTest  // ✅ Spring Boot 전체 컨텍스트가 아니라 JPA 관련된 Bean만 로딩
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)  // ✅ 실제 DB 사용 가능하게 설정 (H2 사용 시 제거 가능)
class ExperienceRepositoryImplTest {

    @Autowired
    private ExperienceRepository experienceRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EntityManager em;

    private User testUser;

    @BeforeEach
    void setUp() {
        // ✅ User 객체 생성 (Builder 사용)
        testUser = User.builder()
                .employeeId("EMP0012")
                .name("홍길동")
                .joinDate(LocalDate.of(2020, 5, 15))
                .department(Department.EUMSEONG1)
                .part(1)
                .level(3)
                .loginId("hong123")
                .password("password")
                .role(Role.USER)
                .profileImageUrl("https://example.com/profile.jpg")
                .totalExp(0) // ✅ 초기 경험치 0 설정
                .build();

        userRepository.save(testUser);
        em.flush();
        em.clear();
    }

    @Test
    void saveExperience_shouldPersistSuccessfully() {
        // Given
        Experience experience = new Experience(testUser, ExpType.인사평가, 4500);
        experienceRepository.save(experience);
        em.flush();
        em.clear();

        // When
        Optional<Experience> savedExp = experienceRepository.findById(experience.getId());

        // Then
        assertThat(savedExp).isPresent();
        assertThat(savedExp.get().getUser().getId()).isEqualTo(testUser.getId());
        assertThat(savedExp.get().getUser().getName()).isEqualTo("홍길동");
        assertThat(savedExp.get().getExpType()).isEqualTo(ExpType.인사평가);
        assertThat(savedExp.get().getExp()).isEqualTo(4500);
        assertThat(userRepository.findById(testUser.getId()).get().getTotalExp()).isEqualTo(4500); // ✅ User의 totalExp 업데이트 확인

        log.info("✅ Saved Experience: {}", savedExp.get());
    }

    @Test
    void findExperienceById_shouldReturnCorrectData() {
        // Given
        Experience experience = new Experience(testUser, ExpType.인사평가, 4500);
        experienceRepository.save(experience);
        em.flush();

        // When
        Optional<Experience> foundExp = experienceRepository.findById(experience.getId());

        // Then
        assertThat(foundExp).isPresent();
        assertThat(foundExp.get().getUser().getId()).isEqualTo(testUser.getId());
        assertThat(foundExp.get().getUser().getName()).isEqualTo("홍길동");
        assertThat(foundExp.get().getExp()).isEqualTo(4500);
        assertThat(foundExp.get().getExpType()).isEqualTo(ExpType.인사평가);

        log.info("✅ Found Experience: {}", foundExp.get());
    }

    @Test
    void findByUserId_shouldReturnAllExperiences() {
        // Given
        Experience experience1 = new Experience(testUser, ExpType.인사평가, 4500);
        Experience experience2 = new Experience(testUser, ExpType.리더_부여_퀘스트, 3000);
        experienceRepository.save(experience1);
        experienceRepository.save(experience2);
        em.flush();

        // When
        List<Experience> expList = experienceRepository.findByUserId(testUser.getId());

        // Then
        assertThat(expList).hasSize(2);
        assertThat(expList.get(0).getUser().getId()).isEqualTo(testUser.getId());
        assertThat(expList.get(1).getUser().getId()).isEqualTo(testUser.getId());

        log.info("✅ Found {} Experience records for user {}: {}", expList.size(), testUser.getName(), expList);
    }
}
