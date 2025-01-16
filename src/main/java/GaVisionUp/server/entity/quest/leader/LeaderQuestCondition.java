package GaVisionUp.server.entity.quest.leader;

import GaVisionUp.server.entity.enums.Cycle;
import GaVisionUp.server.entity.enums.Department;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Table(name = "leader_quest_condition")
public class LeaderQuestCondition {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Department department; // ✅ 소속 (ex: "음성 1센터")

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Cycle cycle; // ✅ 획득 주기 (ex: "월간", "주간")

    @Column(nullable = false)
    private String questName; // ✅ 퀘스트명 (ex: "월특근", "업무개선")

    @Column(nullable = false)
    private double weight; // ✅ 비중 (%) (ex: 60%, 40%)

    @Column(nullable = false)
    private int totalExp; // ✅ 부여 경험치 (ex: 1200, 800)

    @Column(nullable = false)
    private int maxExp; // ✅ Max 기준 경험치 (ex: 100, 67)

    @Column(nullable = false)
    private int medianExp; // ✅ Median 기준 경험치 (ex: 50, 33)

    @Column(nullable = false)
    private String maxCondition; // ✅ Max 조건 (ex: "4회 이상", "업무프로세스 개선 리드자")

    @Column(nullable = false)
    private String medianCondition; // ✅ Median 조건 (ex: "2회 이상", "업무프로세스 개선 참여자")

    @Column(nullable = false)
    private String description; // ✅ 비고 (설명)

    public void updateCondition(double weight, int totalExp, int maxExp, int medianExp, String maxCondition, String medianCondition, String description) {
        this.weight = weight;
        this.totalExp = totalExp;
        this.maxExp = maxExp;
        this.medianExp = medianExp;
        this.maxCondition = maxCondition;
        this.medianCondition = medianCondition;
        this.description = description;
    }
}
