package GaVisionUp.server.web.dto.level;

import GaVisionUp.server.entity.Level;
import lombok.Getter;

@Getter
public class LevelResponse {
    private final String levelName;
    private final int requiredExp;

    public LevelResponse(Level level) {
        this.levelName = level.getLevelName();
        this.requiredExp = level.getRequiredExp();
    }
}