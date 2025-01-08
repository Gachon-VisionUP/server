package GaVisionUp.server.service.exp.personalexp;

import GaVisionUp.server.entity.User;
import GaVisionUp.server.entity.enums.Department;
import GaVisionUp.server.entity.enums.ExpType;
import GaVisionUp.server.entity.exp.ExpBar;
import GaVisionUp.server.entity.exp.PersonalExp;
import GaVisionUp.server.repository.exp.expbar.ExpBarRepository;
import GaVisionUp.server.repository.exp.personalexp.PersonalExpRepository;
import GaVisionUp.server.service.exp.personalexp.PersonalExpService;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Transactional
class PersonalExpServiceImplTest {

    @Autowired
    private PersonalExpService personalExpService;

    @Autowired
    private PersonalExpRepository personalExpRepository;

    @Autowired
    private ExpBarRepository expBarRepository;

    @Autowired
    private EntityManager em;

    private User testUser;
    private ExpBar testExpBar;

    @BeforeEach
    void setUp() {
        // Users 생성 및 저장
        testUser = new User();
        testUser.setName("홍길동");
        testUser.setJoinDate(LocalDate.of(2020, 5, 15));
        testUser.setJoinNumber(12);
        testUser.setJobGroup(1);
        testUser.setLevel(3);
        testUser.setLoginId("hong123");
        testUser.setPassword("password");
        testUser.setTotalExp(0);

        em.persist(testUser);
        em.flush();
        em.clear();

        // ExpBar 생성 및 저장
        testExpBar = new ExpBar();
        testExpBar.setUser(testUser);
        testExpBar.setDepartment(Department.음성1센터);
        testExpBar.setName("홍길동");
        testExpBar.setLevel("F1-Ⅰ");
        testExpBar.setTotalExp(0);

        expBarRepository.save(testExpBar);
        em.flush();
        em.clear();
    }

    @Test
    void savePersonalExp_shouldPersistSuccessfullyAndUpdateExpBar() {
        // Given
        int expToAdd = 4500;

        // When
        PersonalExp savedExp = personalExpService.savePersonalExp(testUser, ExpType.인사평가, expToAdd);
        ExpBar updatedExpBar = expBarRepository.findById(testExpBar.getId()).orElseThrow();

        // Then
        assertThat(savedExp).isNotNull();
        assertThat(savedExp.getUser().getId()).isEqualTo(testUser.getId());
        assertThat(savedExp.getExpType()).isEqualTo(ExpType.인사평가);
        assertThat(savedExp.getExp()).isEqualTo(expToAdd);
        assertThat(updatedExpBar.getTotalExp()).isEqualTo(expToAdd);
    }

    @Test
    void getPersonalExpByUserId_shouldReturnAllPersonalExp() {
        // Given
        personalExpService.savePersonalExp(testUser, ExpType.인사평가, 4500);
        personalExpService.savePersonalExp(testUser, ExpType.리더_부여_퀘스트, 3000);

        // When
        List<PersonalExp> expList = personalExpService.getPersonalExpByUserId(testUser.getId());

        // Then
        assertThat(expList).hasSize(2);
        assertThat(expList.get(0).getUser().getId()).isEqualTo(testUser.getId());
        assertThat(expList.get(1).getUser().getId()).isEqualTo(testUser.getId());
    }

    @Test
    void getPersonalExpById_shouldReturnCorrectData() {
        // Given
        PersonalExp savedExp = personalExpService.savePersonalExp(testUser, ExpType.인사평가, 4500);

        // When
        PersonalExp foundExp = personalExpService.getPersonalExpById(savedExp.getId());

        // Then
        assertThat(foundExp).isNotNull();
        assertThat(foundExp.getUser().getId()).isEqualTo(testUser.getId());
        assertThat(foundExp.getExpType()).isEqualTo(ExpType.인사평가);
        assertThat(foundExp.getExp()).isEqualTo(4500);
    }

    @Test
    void getPersonalExpById_shouldThrowExceptionIfNotFound() {
        // Given
        Long invalidId = 999L;

        // When & Then
        assertThatThrownBy(() -> personalExpService.getPersonalExpById(invalidId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("해당 개인 경험치가 존재하지 않습니다.");
    }

    @Test
    void addExperience_shouldIncreaseTotalExp() {
        // Given
        int additionalExp = 2000;

        // When
        personalExpService.addExperience(testUser.getId(), additionalExp);
        ExpBar updatedExpBar = expBarRepository.findById(testExpBar.getId()).orElseThrow();

        // Then
        assertThat(updatedExpBar.getTotalExp()).isEqualTo(additionalExp);
    }
}
