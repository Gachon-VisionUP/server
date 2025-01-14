package GaVisionUp.server.web.dto.exp.ring;

import GaVisionUp.server.entity.enums.ExpType;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Map;

@Getter
@AllArgsConstructor
public class ExpBarRingYearlyResponse {
    private String levelName;                 // ✅ 현재 레벨명
    private int year;                         // ✅ 조회 연도
    private Map<ExpType, Integer> expByType;  // ✅ 경험치 유형별 누적 경험치
}