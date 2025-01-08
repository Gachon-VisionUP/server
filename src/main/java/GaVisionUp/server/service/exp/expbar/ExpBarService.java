package GaVisionUp.server.service.exp.expbar;

import GaVisionUp.server.entity.exp.ExpBar;

public interface ExpBarService {
    ExpBar createExpBar(ExpBar expBar);
    ExpBar getExpBarById(Long id);
    ExpBar getExpBarByUserId(Long userId);
}
