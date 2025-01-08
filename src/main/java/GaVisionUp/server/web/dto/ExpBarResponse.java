package GaVisionUp.server.web.dto;

import GaVisionUp.server.entity.exp.ExpBar;
import lombok.Getter;

@Getter
public class ExpBarResponse {
    private final Long id;
    private final Long userId;
    private final String department;
    private final String name;
    private final String level;
    private final int totalExp;

    public ExpBarResponse(ExpBar expBar) {
        this.id = expBar.getId();
        this.userId = expBar.getUser().getId();  // ✅ 프록시 방지 (User 직접 조회)
        this.department = expBar.getDepartment().toString();  // ✅ Enum을 문자열로 변환
        this.name = expBar.getName();
        this.level = expBar.getLevel();
        this.totalExp = expBar.getTotalExp();
    }
}