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
    private Department department; // ✅ 부서 정보

    @Column(nullable = false)
    private int part; // ✅ 직무 그룹

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Cycle cycle; // ✅ 주기

    @Column(name = "round_value", nullable = false)
    private int round; // ✅ 주차

    @Column(nullable = false)
    private double productivity; // ✅ 생산성

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ExpType expType; // ✅ 경험치 유형

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TeamQuestGrade questGrade; // ✅ 평가 등급

    @Column(nullable = false)
    private int grantedExp; // ✅ 부여된 경험치

    @Column(nullable = false)
    private LocalDate grantedDate; // ✅ 경험치 부여 날짜

    @Column(nullable = false)
    private double maxCondition; // ✅ MAX 등급 기준 생산성

    @Column(nullable = false)
    private double medCondition; // ✅ MEDIAN 등급 기준 생산성

    @Column(nullable = false)
    private int maxExp; // ✅ MAX 등급 경험치

    @Column(nullable = false)
    private int medExp; // ✅ MEDIAN 등급 경험치

    @Column(nullable = true)
    private String note; // ✅ 비고 (추가 설명)

    /**
     * ✅ JobQuest 생성 메서드
     */
    public static JobQuest create(Department department, int part, Cycle cycle, int round, double productivity,
                                  double maxCondition, double medCondition, int maxExp, int medExp,
                                  TeamQuestGrade questGrade, int grantedExp, String note) {
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
                .note(note)
                .build();
    }

    public void updateJobQuest(double productivity, double maxCondition, double medCondition,
                               TeamQuestGrade grade, int grantedExp, String note) {
        this.productivity = productivity;
        this.maxCondition = maxCondition;
        this.medCondition = medCondition;
        this.questGrade = grade;
        this.grantedExp = grantedExp;
        this.note = note;
        this.grantedDate = LocalDate.now(); // 업데이트된 날짜로 변경
    }
}
