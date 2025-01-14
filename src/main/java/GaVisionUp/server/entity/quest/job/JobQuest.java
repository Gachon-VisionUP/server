package GaVisionUp.server.entity.quest.job;

import GaVisionUp.server.entity.enums.Cycle;
import GaVisionUp.server.entity.enums.Department;
import GaVisionUp.server.entity.enums.ExpType;
import GaVisionUp.server.entity.enums.TeamQuestGrade;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Table(name = "job_quest")
public class JobQuest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Department department;

    @Column(nullable = false)
    private int part;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Cycle cycle;

    @Column(name = "round_value", nullable = false)
    private int round;

    @Column(nullable = false)
    private double productivity;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ExpType expType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TeamQuestGrade questGrade;

    @Column(nullable = false)
    private int grantedExp;

    @Column(nullable = false)
    private LocalDate grantedDate;

    @Column(nullable = false)
    private double maxCondition; // ✅ MAX 등급 기준 생산성

    @Column(nullable = false)
    private double medCondition; // ✅ MEDIAN 등급 기준 생산성

    @Column(nullable = false)
    private int maxExp; // ✅ MAX 등급 경험치

    @Column(nullable = false)
    private int medExp; // ✅ MEDIAN 등급 경험치

    public static JobQuest create(Department department, int part, Cycle cycle, int round, double productivity,
                                  double maxCondition, double medCondition, int maxExp, int medExp,
                                  TeamQuestGrade questGrade, int grantedExp) {
        return JobQuest.builder()
                .department(department)
                .part(part)
                .cycle(cycle)
                .round(round)
                .productivity(productivity)
                .maxCondition(maxCondition)
                .medCondition(medCondition)
                .maxExp(maxExp)
                .medExp(medExp)
                .expType(ExpType.JOB_QUEST)
                .questGrade(questGrade)
                .grantedExp(grantedExp)
                .grantedDate(LocalDate.now())
                .build();
    }
}
