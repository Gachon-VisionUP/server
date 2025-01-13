package GaVisionUp.server.web.dto.exp.list;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ExperienceStateResponse {
    private String currentLevel;      // 현재 레벨명
    private int totalExp;             // 총 경험치
    private String nextLevel;         // 다음 레벨명
    private int nextLevelExpRequired; // 다음 레벨까지 필요 경험치
    private int currentYearExp;       // 올해 누적 경험치
    private int previousYearExp;      // 작년 누적 경험치
}