package GaVisionUp.server.web.dto;

import GaVisionUp.server.entity.exp.ExpBar;
import lombok.Getter;

@Getter
public class ExpBarResponse {
    private final Long id;
    private final Long userId;
    private final String name;
    private final String level;
    private final int totalExp;
    private final int currentTotalExp;
    private final int previousTotalExp;

    public ExpBarResponse(ExpBar expBar) {
        this.id = expBar.getId();
        this.userId = expBar.getUser().getId();
        this.name = expBar.getUser().getName();
        this.level = expBar.getUser().getLevel();
        this.totalExp = expBar.getUser().getTotalExp();
        this.currentTotalExp = expBar.getCurrentTotalExp();  // ✅ 현재 연도 총 경험치
        this.previousTotalExp = expBar.getPreviousTotalExp();  // ✅ 이전 연도 총 경험치
    }
}
