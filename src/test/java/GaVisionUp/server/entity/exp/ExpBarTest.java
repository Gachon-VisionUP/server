package GaVisionUp.server.entity.exp;

import GaVisionUp.server.entity.enums.Department;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ExpBarTest {
    @Test
    void createExpBarTest() {
        // 가상의 데이터 생성
        ExpBar expBar = new ExpBar();
        expBar.setId(1L);
        expBar.setUserId(1001);
        expBar.setDepartment(Department.음성1센터);
        expBar.setName("홍길동");
        expBar.setLevel("F1-Ⅰ");
        expBar.setTotalExp(500);

        // 데이터 출력
        System.out.println(expBar);

        // 값이 정상적으로 세팅되었는지 검증
        assertEquals(1001, expBar.getUserId());
        assertEquals(Department.음성1센터, expBar.getDepartment());
        assertEquals("홍길동", expBar.getName());
        assertEquals("F1-Ⅰ", expBar.getLevel());
        assertEquals(500, expBar.getTotalExp());
    }
}
