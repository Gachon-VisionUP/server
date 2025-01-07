package GaVisionUp.server.service.exp;

import GaVisionUp.server.entity.exp.ExpBar;

public interface ExpBarService {
    ExpBar createExpBar(ExpBar expBar);
    ExpBar getExpBarByUserId(int userId);
    ExpBar addExperience(int userId, int experience);
}
