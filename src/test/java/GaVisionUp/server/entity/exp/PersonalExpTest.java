package GaVisionUp.server.entity.exp;

import GaVisionUp.server.entity.Users;
import GaVisionUp.server.entity.enums.ExpType;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class PersonalExpTest {

    @Test
    void createPersonalExpTest() {
        // Given: 가상의 Users 및 ExpBar 객체 생성
        Users user = new Users();
        user.setId(1001L);
        user.setName("홍길동");

        ExpBar expBar = new ExpBar();
        expBar.setId(1L);
        expBar.setUserId(1001);
        expBar.setName("홍길동");
        expBar.setLevel("F1-Ⅰ");
        expBar.setTotalExp(500);

        // Given: PersonalExp 객체 생성
        PersonalExp personalExp = new PersonalExp(user, ExpType.인사평가, 4500, expBar);

        // When & Then: 값 검증
        assertEquals(user, personalExp.getUsers());
        assertEquals(ExpType.인사평가, personalExp.getExpType());
        assertEquals(4500, personalExp.getExp());
        assertEquals(LocalDate.now(), personalExp.getObtainedDate());
        assertEquals(expBar, personalExp.getExpBar());

        // 객체 정보 출력
        System.out.println(personalExp.getClass());
    }
}
