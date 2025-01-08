package GaVisionUp.server.service.exp.experience;

import GaVisionUp.server.entity.User;
import GaVisionUp.server.entity.enums.Department;
import GaVisionUp.server.entity.enums.ExpType;
import GaVisionUp.server.entity.enums.Role;
import GaVisionUp.server.entity.exp.Experience;
import GaVisionUp.server.repository.exp.experience.ExperienceRepository;
import GaVisionUp.server.repository.user.UserRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DataJpaTest // ✅ JPA 관련 Bean만 로드 (SpringBootTest 대신 사용)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE) // ✅ 실제 DB 사용 (H2가 아니라면 필요)
class ExperienceServiceImplTest {

    @Autowired
    private ExperienceRepository experienceRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private ExperienceService experienceService;

    private User testUser;

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
                .loginId("hong123")
                .password("password")
                .role(Role.USER)
                .profileImageUrl("https://example.com/profile.jpg")
                .totalExp(0) // 초기 경험치
                .build();

        userRepository.save(testUser);
        em.flush();
        em.clear();
    }

    @Test
    void saveExperience_shouldPersistSuccessfullyAndUpdateUserTotalExp() {
        // Given
        int expToAdd = 4500;

        // When
        Experience savedExp = experienceService.addExperience(testUser.getId(), ExpType.인사평가, expToAdd);
        User updatedUser = em.find(User.class, testUser.getId());

        // Then
        assertThat(savedExp).isNotNull();
        assertThat(savedExp.getUser().getId()).isEqualTo(testUser.getId());
        assertThat(savedExp.getExpType()).isEqualTo(ExpType.인사평가);
        assertThat(savedExp.getExp()).isEqualTo(expToAdd);
        assertThat(updatedUser.getTotalExp()).isEqualTo(expToAdd); // ✅ User의 totalExp 증가 확인
    }

    @Test
    void getExperienceByUserId_shouldReturnAllExperiences() {
        // Given
        experienceService.addExperience(testUser.getId(), ExpType.인사평가, 4500);
        experienceService.addExperience(testUser.getId(), ExpType.리더_부여_퀘스트, 3000);

        // When
        List<Experience> expList = experienceService.getExperiencesByUserId(testUser.getId());

        // Then
        assertThat(expList).hasSize(2);
        assertThat(expList.get(0).getUser().getId()).isEqualTo(testUser.getId());
        assertThat(expList.get(1).getUser().getId()).isEqualTo(testUser.getId());
    }

    @Test
    void getExperienceById_shouldReturnCorrectData() {
        // Given
        Experience savedExp = experienceService.addExperience(testUser.getId(), ExpType.인사평가, 4500);

        // When
        Optional<Experience> foundExp = experienceService.getExperienceById(savedExp.getId());  // ✅ 메서드명 수정

        // Then
        assertThat(foundExp).isNotNull();
        assertThat(foundExp.get().getUser().getId()).isEqualTo(testUser.getId());
        assertThat(foundExp.get().getExpType()).isEqualTo(ExpType.인사평가);
        assertThat(foundExp.get().getExp()).isEqualTo(4500);
    }

    @Test
    void getExperienceById_shouldThrowExceptionIfNotFound() {
        // Given
        Long invalidId = 999L;

        // When & Then
        assertThatThrownBy(() -> experienceService.getExperienceById(invalidId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("해당 경험치 기록이 존재하지 않습니다.");
    }

    @Test
    void addExperience_shouldIncreaseTotalExp() {
        // Given
        int additionalExp = 2000;

        // When
        experienceService.addExperience(testUser.getId(), ExpType.리더_부여_퀘스트, additionalExp);
        User updatedUser = em.find(User.class, testUser.getId());

        // Then
        assertThat(updatedUser.getTotalExp()).isEqualTo(additionalExp); // ✅ User의 totalExp 증가 확인
    }
}
