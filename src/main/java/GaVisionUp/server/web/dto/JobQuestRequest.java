package GaVisionUp.server.web.dto;

import GaVisionUp.server.entity.enums.Department;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class JobQuestRequest {
    private String department;  // ✅ 소속 (ex: "음성 1센터")
    private int part;  // ✅ 직무 그룹 (ex: 1)
    private String cycle;  // ✅ 주기 (ex: "주간", "월간") 추가됨
    private int round;  // ✅ 회차 (ex: 1~52, 1~12) → 기존 week를 변경
    private double productivity;  // ✅ 생산성 (ex: 5.2)
}
