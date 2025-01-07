package GaVisionUp.server.repository.exp.personalexp;

import GaVisionUp.server.entity.Users;
import GaVisionUp.server.entity.enums.Department;
import GaVisionUp.server.entity.enums.ExpType;
import GaVisionUp.server.entity.exp.ExpBar;
import GaVisionUp.server.entity.exp.PersonalExp;
import GaVisionUp.server.repository.exp.expbar.ExpBarRepository;
import GaVisionUp.server.repository.exp.personalexp.PersonalExpRepository;
import jakarta.persistence.EntityManager;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
@SpringBootTest
@Transactional
class PersonalExpRepositoryImplTest {

    @Autowired
    private PersonalExpRepository personalExpRepository;

    @Autowired
    private ExpBarRepository expBarRepository;


    @Autowired
    private EntityManager em;

    private Users testUser;
    private ExpBar testExpBar;

    @BeforeEach
    void setUp() {
        // Given: 테스트용 Users 데이터 생성 및 저장
        testUser = new Users();
        testUser.setName("홍길동");
        testUser.setJoinDate(LocalDate.of(2020, 5, 15));
        testUser.setJoinNumber(12);
        testUser.setJobGroup(1);
        testUser.setLevel(3);
        testUser.setLoginId("hong123");
        testUser.setPassword("password");
        testUser.setTotalExp(0);

        em.persist(testUser);  // Users 저장
        em.flush();  // ★ DB에 반영
        em.clear();  // ★ 영속성 컨텍스트 초기화

        // Given: ExpBar 데이터 생성 및 저장
        testExpBar = new ExpBar();
        testExpBar.setUserId(testUser.getId());  // testUser의 ID 참조
        testExpBar.setDepartment(Department.음성1센터);
        testExpBar.setName("홍길동");
        testExpBar.setLevel("F1-Ⅰ");
        testExpBar.setTotalExp(0);

        expBarRepository.save(testExpBar);  // ExpBar 저장
        em.flush();  // ★ DB에 반영
        em.clear();  // ★ 영속성 컨텍스트 초기화
    }

    @Test
    void savePersonalExp_shouldPersistSuccessfully() {
        // Given
        PersonalExp personalExp = new PersonalExp(testUser, ExpType.인사평가, 4500, testExpBar);

        // When
        PersonalExp savedExp = personalExpRepository.save(personalExp);
        em.flush();  // ★ DB 반영

        // Then
        assertThat(savedExp).isNotNull();
        assertThat(savedExp.getUsers().getName()).isEqualTo("홍길동");
        assertThat(savedExp.getExpType()).isEqualTo(ExpType.인사평가);
        assertThat(savedExp.getExp()).isEqualTo(4500);
        assertThat(savedExp.getExpBar()).isEqualTo(testExpBar);

        // 로그 출력
        log.info("Saved PersonalExp: {}", savedExp);
    }


    @Test
    void findPersonalExpById_shouldReturnCorrectData() {
        // Given
        PersonalExp personalExp = new PersonalExp(testUser, ExpType.인사평가, 4500, testExpBar);
        personalExpRepository.save(personalExp);

        // When
        Optional<PersonalExp> foundExp = personalExpRepository.findById(personalExp.getId());

        // Then
        assertThat(foundExp).isPresent();
        assertThat(foundExp.get().getUsers().getName()).isEqualTo("홍길동");
        assertThat(foundExp.get().getExp()).isEqualTo(4500);
        assertThat(foundExp.get().getExpType()).isEqualTo(ExpType.인사평가);

        // 로그 출력
        log.info("Found PersonalExp: {}", foundExp.get());
    }

    @Test
    void findByUserId_shouldReturnAllPersonalExp() {
        // Given
        PersonalExp personalExp1 = new PersonalExp(testUser, ExpType.인사평가, 4500, testExpBar);
        PersonalExp personalExp2 = new PersonalExp(testUser, ExpType.리더_부여_퀘스트, 3000, testExpBar);
        personalExpRepository.save(personalExp1);
        personalExpRepository.save(personalExp2);

        // When
        List<PersonalExp> expList = personalExpRepository.findByUserId(testUser.getId());

        // Then
        assertThat(expList).hasSize(2);

        // 로그 출력
        log.info("Found {} PersonalExp records for user {}: {}", expList.size(), testUser.getName(), expList);
    }

    @Test
    void addExperience_shouldIncreaseTotalExp() {
        // Given
        int additionalExp = 2000;

        // When
        personalExpRepository.addExperience(testUser.getId(), additionalExp);
        ExpBar updatedExpBar = expBarRepository.findById(testExpBar.getId()).orElseThrow();

        // Then
        assertThat(updatedExpBar.getTotalExp()).isEqualTo(2000);

        // 로그 출력
        log.info("Updated ExpBar after adding experience: {}", updatedExpBar);
    }
}