package GaVisionUp.server.entity.exp;

import GaVisionUp.server.entity.User;
import GaVisionUp.server.entity.enums.ExpType;
import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;


@Data
@Entity
@Table(name = "personal_exp")
public class PersonalExp {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;


    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ExpType expType; // 경험치 유형 (예: 인사평가, 직무별 퀘스트)

    @Column(nullable = false)
    private int exp;  // 획득한 경험치

    @Column(nullable = false)
    private LocalDate obtainedDate; // 경험치 획득 날짜

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "exp_bar_id", nullable = false)
    private ExpBar expBar;  // ExpBar에 총 경험치 축적

    public PersonalExp(User user, ExpType expType, int exp, ExpBar expBar) {
        this.user = user;
        this.expType = expType;
        this.exp = exp;
        this.obtainedDate = LocalDate.now();
        this.expBar = expBar;
    }

    public PersonalExp() {}
}