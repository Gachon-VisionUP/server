package GaVisionUp.server.entity.exp;

import GaVisionUp.server.entity.User;
import GaVisionUp.server.entity.enums.Department;
import GaVisionUp.server.entity.enums.ExpType;
import GaVisionUp.server.entity.enums.Role;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class PersonalExpTest {

    @Test
    void createPersonalExpTest() {
        // Given: User 객체 생성
        User user = User.builder()
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

        // Given: ExpBar 객체 생성
        ExpBar expBar = new ExpBar();
        expBar.setId(1L);
        expBar.setUser(user);
        expBar.setDepartment(user.getDepartment()); // User의 department 사용
        expBar.setName(user.getName());
        expBar.setLevel("F1-Ⅰ");
        expBar.setTotalExp(500);

        // Given: PersonalExp 객체 생성
        PersonalExp personalExp = new PersonalExp(user, ExpType.인사평가, 4500, expBar);

        // When & Then: 값 검증
        assertEquals(user, personalExp.getUser());
        assertEquals(ExpType.인사평가, personalExp.getExpType());
        assertEquals(4500, personalExp.getExp());
        assertEquals(LocalDate.now(), personalExp.getObtainedDate());
        assertEquals(expBar, personalExp.getExpBar());

        // 객체 정보 출력
        System.out.println(personalExp);
    }
}
