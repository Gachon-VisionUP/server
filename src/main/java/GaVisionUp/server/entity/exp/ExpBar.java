package GaVisionUp.server.entity.exp;

import GaVisionUp.server.entity.Level;
import GaVisionUp.server.entity.User;
import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "exp_bar")
public class ExpBar {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false)
    private User user; // ✅ 유저 정보 참조

    @Column(nullable = false)
    private String levelName; // ✅ 레벨 이름 저장 (프록시 문제 방지)

    @Column(nullable = false)
    private int currentTotalExp = 0;

    @Column(nullable = false)
    private int previousTotalExp = 0;

    // 기본 생성자
    public ExpBar() {}

    // ✅ 필수 필드를 포함하는 생성자
    public ExpBar(User user) {
        this.user = user;
        this.levelName = user.getLevel().getLevelName(); // ✅ 초기 생성 시 레벨 저장
    }

    // ✅ 레벨 업데이트 메서드 추가
    public void updateLevel() {
        this.levelName = user.getLevel().getLevelName(); // ✅ 유저의 현재 레벨을 반영
    }
}
