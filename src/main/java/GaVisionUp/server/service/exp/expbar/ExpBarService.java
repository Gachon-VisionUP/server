package GaVisionUp.server.service.exp.expbar;

import GaVisionUp.server.entity.exp.ExpBar;

public interface ExpBarService {
    ExpBar createExpBar(ExpBar expBar);
    ExpBar getExpBarByUserId(Long userId);
    ExpBar addExperience(Long userId, int experience);
}
