package GaVisionUp.server.entity.exp;

import GaVisionUp.server.entity.enums.Department;
import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
public class ExpBar {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long userId; // 사번

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Department department; // 소속

    @Column(nullable = false)
    private String name; // 사원 이름

    @Column(nullable = false)
    private String level; // 레벨 (NULL 방지)

    @Column(nullable = false)
    private int totalExp; // 총 경험치 (NULL 방지)

    // 기본 생성자
    public ExpBar() {}

    // 필수 필드를 포함하는 생성자
    public ExpBar(Long userId, Department department, String name, String level, int totalExp) {
        this.userId = userId;
        this.department = department;
        this.name = name;
        this.level = level;
        this.totalExp = totalExp;
    }
}