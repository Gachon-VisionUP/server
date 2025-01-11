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
    private Department department;

    @Column(nullable = false)
    private int part;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Cycle cycle;

    @Column(name = "month_value", nullable = false)
    private int month; // ✅ 월 정보 유지

    @Column(name = "round_value", nullable = false)
    private int round; // ✅ 주차 대신 round 값 저장

    @Column(nullable = false)
    private double sales;

    @Column(nullable = false)
    private double laborCost;

    @Column(nullable = false)
    private LocalDate recordedDate;

    public static JobQuestDetail create(Department department, int part, Cycle cycle, int month, int round, double sales, double laborCost, LocalDate recordedDate) {
        return JobQuestDetail.builder()
                .department(department)
                .part(part)
                .cycle(cycle)
                .month(month)
                .round(round)
                .sales(sales)
                .laborCost(laborCost)
                .recordedDate(recordedDate)
                .build();
    }
}
