package GaVisionUp.server.entity.exp;

import GaVisionUp.server.entity.User;
import GaVisionUp.server.entity.enums.Department;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ExpBarTest {

    private User testUser;

    @BeforeEach
    void setUp() {
        // Users 생성 및 저장
        testUser = new User();
        testUser.setId(1001L);
        testUser.setName("홍길동");
        testUser.setJoinDate(LocalDate.of(2020, 5, 15));
        testUser.setJoinNumber(12);
        testUser.setJobGroup(1);
        testUser.setLevel(3);
        testUser.setLoginId("hong123");
        testUser.setPassword("password");
        testUser.setTotalExp(0);
    }
    @Test
    void createExpBarTest() {
        // 가상의 데이터 생성
        ExpBar expBar = new ExpBar();
        expBar.setId(1L);
        expBar.setUser(testUser);
        expBar.setDepartment(Department.음성1센터);
        expBar.setName("홍길동");
        expBar.setLevel("F1-Ⅰ");
        expBar.setTotalExp(500);

        // 데이터 출력
        System.out.println(expBar);

        // 값이 정상적으로 세팅되었는지 검증
        assertEquals(1001, expBar.getUser().getId());
        assertEquals(Department.음성1센터, expBar.getDepartment());
        assertEquals("홍길동", expBar.getName());
        assertEquals("F1-Ⅰ", expBar.getLevel());
        assertEquals(500, expBar.getTotalExp());
    }
}
