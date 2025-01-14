package GaVisionUp.server.web.dto.exp.ring;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ExpBarRingResponse {
    private String levelName;      // ✅ 현재 레벨명
    private int totalExp;          // ✅ 전체 누적 경험치
    private int currentYearExp;    // ✅ 올해 누적 경험치
    private int previousYearExp;   // ✅ 작년까지 누적 경험치
}