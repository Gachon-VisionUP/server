package GaVisionUp.server.entity;

import GaVisionUp.server.entity.enums.ExpType;
import GaVisionUp.server.entity.enums.PerformanceGrade;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Table(name = "performance_review")
public class PerformanceReview {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false)
    private User user; // ✅ 사번 (대상자)

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ExpType expType; // ✅ 상반기/하반기 인사평가 구분

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PerformanceGrade grade; // ✅ 인사평가 등급 (S, A, B, C, D)

    @Column(nullable = false)
    private int grantedExp; // ✅ 부여 경험치

    @Column(nullable = false)
    private LocalDate evaluationDate; // ✅ 평가 날짜

    public static PerformanceReview create(User user, ExpType expType, PerformanceGrade grade) {
        return PerformanceReview.builder()
                .user(user)
                .expType(expType)
                .grade(grade)
                .grantedExp(grade.getExp()) // ✅ 등급에 따른 경험치 자동 할당
                .evaluationDate(LocalDate.now())
                .build();
    }

    public void updateReview(PerformanceGrade grade,int newExp,ExpType expType){
        this.grade = grade;
        this.grantedExp = newExp;
        this.expType = expType;
    }
}
