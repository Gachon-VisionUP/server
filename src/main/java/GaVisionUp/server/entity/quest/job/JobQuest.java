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
    private Department department; // ✅ 소속

    @Column(nullable = false)
    private int part; // ✅ 직무 그룹

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Cycle cycle; // ✅ 주기 (MONTHLY, WEEKLY)

    @Column(name = "round_value", nullable = false)
    private int round; // ✅ 연속된 월 또는 주 (CYCLE에 따라 다름)

    @Column(name = "month_value", nullable = false)
    private int month; // ✅ 월 (1~12)

    @Column(name = "week_value", nullable = true)
    private Integer week; // ✅ 주차 (1~5) - 월간 데이터는 null

    @Column(nullable = false)
    private double productivity; // ✅ 생산성

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ExpType expType; // ✅ ExpType.JOB_QUEST

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TeamQuestGrade questGrade; // ✅ 부여된 등급

    @Column(nullable = false)
    private int grantedExp; // ✅ 부여 경험치

    @Column(nullable = false)
    private LocalDate grantedDate; // ✅ 경험치 부여 날짜

    public static JobQuest create(Department department, int part, Cycle cycle, int round, int month, Integer week, double productivity, TeamQuestGrade questGrade, int grantedExp) {

        return JobQuest.builder()
                .department(department)
                .part(part)
                .cycle(cycle)
                .round(round)
                .month(month)
                .week(week)
                .productivity(productivity)
                .expType(ExpType.JOB_QUEST)
                .questGrade(questGrade)
                .grantedExp(grantedExp)
                .grantedDate(LocalDate.now())
                .build();
    }
}
