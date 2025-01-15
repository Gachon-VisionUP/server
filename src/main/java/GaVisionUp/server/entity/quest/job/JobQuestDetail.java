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
    private double sales; // ✅ 매출

    @Column(nullable = false)
    private double designCost; // ✅ 설계용역비

    @Column(nullable = false)
    private double employeeSalary; // ✅ 직원급여

    @Column(nullable = false)
    private double retirementSalary; // ✅ 퇴직급여

    @Column(nullable = false)
    private double insuranceFee; // ✅ 4대보험료

    @Column(nullable = false)
    private double laborCost; // ✅ 인건비 (자동 계산)

    @Column(nullable = false)
    private LocalDate recordedDate;

    /**
     * ✅ JobQuestDetail 생성 (인건비 자동 계산 포함)
     */
    public static JobQuestDetail create(
            Department department, int part, Cycle cycle, int month, int round, double sales,
            double designCost, double employeeSalary, double retirementSalary, double insuranceFee,
            LocalDate recordedDate) {

        double laborCost = designCost + employeeSalary + retirementSalary + insuranceFee; // ✅ 인건비 자동 계산

        return JobQuestDetail.builder()
                .department(department)
                .part(part)
                .cycle(cycle)
                .month(month)
                .round(round)
                .sales(sales)
                .designCost(designCost)
                .employeeSalary(employeeSalary)
                .retirementSalary(retirementSalary)
                .insuranceFee(insuranceFee)
                .laborCost(laborCost) // ✅ 인건비 저장
                .recordedDate(recordedDate)
                .build();
    }

    /**
     * ✅ 기존 데이터 업데이트 (변동 사항 반영)
     */
    public void updateJobQuest(double sales, double designCost, double employeeSalary, double retirementSalary, double insuranceFee) {
        this.sales = sales;
        this.designCost = designCost;
        this.employeeSalary = employeeSalary;
        this.retirementSalary = retirementSalary;
        this.insuranceFee = insuranceFee;
        this.laborCost = designCost + employeeSalary + retirementSalary + insuranceFee; // ✅ 인건비 자동 계산
    }
}
