package GaVisionUp.server.entity;

import GaVisionUp.server.entity.enums.Department;
import GaVisionUp.server.entity.enums.ExpType;
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
    private Department department; // ✅ 소속 (ex: "음성 1센터")

    @Column(nullable = false)
    private int part; // ✅ 직무 그룹 (ex: 1)

    @Column(nullable = false)
    private int round; // ✅ 회차 (ex: 1~52, 1~12)

    @Column(nullable = false)
    private String cycle; // ✅ 주기 (ex: "주간", "월간")

    @Column(nullable = false)
    private double productivity; // ✅ 생산성 (5.1 이상이면 Max 등급)

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ExpType expType; // ✅ ExpType.JOB_QUEST

    @Column(nullable = false)
    private int grantedExp; // ✅ 부여 경험치 (80, 40, 0)

    @Column(nullable = false)
    private LocalDate grantedDate; // ✅ 경험치 부여 날짜

    public static JobQuest create(Department department, int part, int round, String cycle, double productivity, int grantedExp) {
        return JobQuest.builder()
                .department(department)
                .part(part)
                .round(round)
                .cycle(cycle)
                .productivity(productivity)
                .expType(ExpType.JOB_QUEST)
                .grantedExp(grantedExp)
                .grantedDate(LocalDate.now())
                .build();
    }
}
