package GaVisionUp.server.entity.exp;

import GaVisionUp.server.entity.User;
import GaVisionUp.server.entity.enums.Department;
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
    private User user; // 사번 User에서 받아옴

    @Column(nullable = false)
    private int currentTotalExp = 0;

    @Column(nullable = false)
    private int previousTotalExp = 0;
    // 기본 생성자
    public ExpBar() {}

    // 필수 필드를 포함하는 생성자
    public ExpBar(User user) {
        this.user = user;
    }
}