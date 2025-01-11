package GaVisionUp.server.entity.quest.job;

import GaVisionUp.server.entity.enums.Cycle;
import GaVisionUp.server.entity.enums.Department;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Table(name = "job_quest_detail")
public class JobQuestDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Department department; // ✅ 소속 (ex: "음성 1센터")

    @Column(nullable = false)
    private int part; // ✅ 직무 그룹 (ex: 1)

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Cycle cycle; // ✅ 주기 (ex: "주간", "월간")

    @Column(name = "month_value", nullable = false)
    private int month; // ✅ 월 정보 추가 (1~12)

    @Column(name = "week_value", nullable = true)
    private Integer week; // ✅ 주차 정보 추가 (1~5, 월간일 경우 NULL 허용)

    @Column(nullable = false)
    private double sales; // ✅ 매출 (ex: 1000000)

    @Column(nullable = false)
    private double laborCost; // ✅ 인건비 (ex: 500000)

    @Column(nullable = false)
    private LocalDate recordedDate; // ✅ 사용자가 입력한 날짜

    public static JobQuestDetail create(Department department, int part, Cycle cycle, int month, Integer week, double sales, double laborCost, LocalDate recordedDate) {
        return JobQuestDetail.builder()
                .department(department)
                .part(part)
                .cycle(cycle)
                .month(month) // ✅ 추가된 필드 반영
                .week(week) // ✅ 추가된 필드 반영
                .sales(sales)
                .laborCost(laborCost)
                .recordedDate(recordedDate) // ✅ 사용자 입력 날짜 반영
                .build();
    }
}
