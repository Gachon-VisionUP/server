package GaVisionUp.server.entity.exp;

import GaVisionUp.server.entity.Level;
import GaVisionUp.server.entity.User;
import GaVisionUp.server.entity.enums.Department;
import GaVisionUp.server.entity.enums.JobGroup;
import GaVisionUp.server.entity.enums.Role;
import GaVisionUp.server.repository.level.LevelRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class ExpBarTest {

    private User testUser;
    private Level testLevel;

    @BeforeEach
    void setUp() {
        // ✅ 테스트용 Level 엔티티 생성 (F1-Ⅰ)
        testLevel = new Level(JobGroup.F, "F1-Ⅰ", 0);

        // ✅ User 객체를 Builder 패턴을 사용하여 생성
        testUser = User.builder()
                .id(1001L)
                .employeeId("EMP0012") // 사번 추가
                .name("홍길동")
                .joinDate(LocalDate.of(2020, 5, 15))
                .department(Department.EUMSEONG1) // 소속 추가
                .part(1) // 직무 그룹
                .level(testLevel) // ✅ 레벨을 Level 엔티티로 설정
                .loginId("hong123")
                .password("password")
                .role(Role.USER) // 역할 추가
                .profileImageUrl("https://example.com/profile.jpg") // 프로필 이미지 추가
                .totalExp(0)
                .build();
    }

    @Test
    void createExpBarTest() {
        // ✅ ExpBar 객체 생성 및 설정
        ExpBar expBar = new ExpBar();
        expBar.setId(1L);
        expBar.setUser(testUser);
        testUser.addExperience(500);

        // ✅ 데이터 출력
        System.out.println(expBar);

        // ✅ 값이 정상적으로 세팅되었는지 검증
        assertEquals(1001L, expBar.getUser().getId());
        assertEquals(Department.EUMSEONG1, expBar.getUser().getDepartment());
        assertEquals("홍길동", expBar.getUser().getName());
        assertEquals("F1-Ⅰ", expBar.getUser().getLevel().getLevelName()); // ✅ 변경된 부분 (Level 엔티티에서 levelName 조회)
        assertEquals(500, expBar.getUser().getTotalExp());
    }
}
