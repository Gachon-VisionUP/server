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

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Department department; // 소속

    @Column(nullable = false)
    private String name; // 사원 이름

    @Column(nullable = false)
    private String level; // 레벨 (NULL 방지)


    // 기본 생성자
    public ExpBar() {}

    // 필수 필드를 포함하는 생성자
    public ExpBar(User user, Department department, String name, String level) {
        this.user = user;
        this.department = department;
        this.name = name;
        this.level = level;
    }
}