package GaVisionUp.server.web.dto.exp.list;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class ExperienceStateResponse {
    private final String currentLevel;
    private final int totalExp;
    private final String nextLevel;
    private final int nextLevelExpRequirement;
    private final int nextLevelTotalExpRequirement;  // ✅ 추가됨
    private final int currentYearTotalExp;
    private final int previousYearTotalExp;
}