package GaVisionUp.server.entity.exp;

import GaVisionUp.server.entity.User;
import GaVisionUp.server.entity.enums.Department;
import GaVisionUp.server.entity.enums.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ExpBarTest {

    private User testUser;

    @BeforeEach
    void setUp() {
        // User 객체를 Builder 패턴을 사용하여 생성
        testUser = User.builder()
                .id(1001L)
                .employeeId("EMP0012") // 사번 추가
                .name("홍길동")
                .joinDate(LocalDate.of(2020, 5, 15))
                .department(Department.음성1센터) // 소속 추가
                .part(1) // 직무 그룹
                .level(3) // 레벨
                .loginId("hong123")
                .password("password")
                .role(Role.USER) // 역할 추가
                .profileImageUrl("https://example.com/profile.jpg") // 프로필 이미지 추가
                .totalExp(0)
                .build();
    }

    @Test
    void createExpBarTest() {
        // ExpBar 객체 생성 및 설정
        ExpBar expBar = new ExpBar();
        expBar.setId(1L);
        expBar.setUser(testUser);
        expBar.setDepartment(testUser.getDepartment()); // User의 department 사용
        expBar.setName(testUser.getName());
        expBar.setLevel("F1-Ⅰ");
        expBar.setTotalExp(500);

        // 데이터 출력
        System.out.println(expBar);

        // 값이 정상적으로 세팅되었는지 검증
        assertEquals(1001L, expBar.getUser().getId());
        assertEquals(Department.음성1센터, expBar.getDepartment());
        assertEquals("홍길동", expBar.getName());
        assertEquals("F1-Ⅰ", expBar.getLevel());
        assertEquals(500, expBar.getTotalExp());
    }
}
