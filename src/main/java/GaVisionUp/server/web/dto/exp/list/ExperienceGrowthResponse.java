package GaVisionUp.server.web.dto.exp.list;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class ExperienceGrowthResponse {
    private final String nextLevel;  // 다음 레벨명
    private final int nextLevelTotalExpRequirement; // 다음 레벨 총 필요 경험치
    private final int previousYearTotalExp;  // 작년까지 누적된 경험치
    private final int previousExpPercentage; // 작년까지 누적된 경험치 퍼센트 (소수점X)
    private final int currentYearTotalExp; // 올해 획득한 경험치
    private final int currentYearExpPercentage; // 올해 경험치 퍼센트 (소수점X)
}