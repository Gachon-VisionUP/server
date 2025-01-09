package GaVisionUp.server.web.dto;

import GaVisionUp.server.entity.exp.ExpBar;
import lombok.Getter;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Getter
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"}) // ✅ Hibernate 프록시 문제 해결
public class ExpBarResponse {
    private final Long id;
    private final Long userId;
    private final String name;
    private final String levelName;  // ✅ ExpBar에서 levelName 바로 가져오기
    private final int totalExp;
    private final int currentTotalExp;
    private final int previousTotalExp;

    public ExpBarResponse(ExpBar expBar) {
        this.id = expBar.getId();
        this.userId = expBar.getUser().getId();
        this.name = expBar.getUser().getName();
        this.levelName = expBar.getLevelName();  // ✅ ExpBar에 저장된 levelName 사용
        this.totalExp = expBar.getUser().getTotalExp();
        this.currentTotalExp = expBar.getCurrentTotalExp();
        this.previousTotalExp = expBar.getPreviousTotalExp();
    }
}
