package GaVisionUp.server.web.dto.quest.job.detail;

import GaVisionUp.server.entity.enums.Cycle;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@NoArgsConstructor
public class JobQuestDetailRequest {
    private String department;  // ✅ 소속 (ex: "음성 1센터")
    private int part;  // ✅ 직무 그룹 (ex: 1)
    private Cycle cycle;  // ✅ 주기 (ex: "주간", "월간")
    private int round;  // ✅ 회차 (ex: 1~52)
    private double sales;  // ✅ 매출
    private double laborCost;  // ✅ 인건비
    private LocalDate recordedDate;  // ✅ 사용자 입력 날짜
}