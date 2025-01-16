package GaVisionUp.server.entity.exp;

import GaVisionUp.server.entity.User;
import GaVisionUp.server.entity.enums.ExpType;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;

@Data
@Entity
@Table(name = "experience")
public class Experience {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ExpType expType; // 경험치 유형 (예: 인사평가, 직무별 퀘스트)

    @Column(nullable = false)
    private int exp;  // 획득한 경험치

    @Column(nullable = false)
    private LocalDate obtainedDate; // 경험치 획득 날짜

    // ✅ ExpBar 제거: User의 totalExp를 직접 업데이트하는 방식으로 변경
    public Experience(User user, ExpType expType, int exp) {
        this.user = user;
        this.expType = expType;
        this.exp = exp;
        this.obtainedDate = LocalDate.now();
    }
    public Experience() {}
}