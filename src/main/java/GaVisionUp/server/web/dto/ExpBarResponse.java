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
    private final String levelName;
    private final int totalExp;
    private final int currentTotalExp;
    private final int previousTotalExp;

    public ExpBarResponse(ExpBar expBar) {
        this.id = expBar.getId();
        this.userId = expBar.getUser().getId();  // ✅ Lazy Loading 문제 발생 지점
        this.name = expBar.getUser().getName();
        this.levelName = expBar.getUser().getLevel().getLevelName(); // ✅ Level 엔티티에서 이름 추출
        this.totalExp = expBar.getUser().getTotalExp();
        this.currentTotalExp = expBar.getCurrentTotalExp();
        this.previousTotalExp = expBar.getPreviousTotalExp();
    }
}

