package GaVisionUp.server.service.level;

import GaVisionUp.server.entity.Level;
import GaVisionUp.server.entity.enums.JobGroup;

import java.util.List;

public interface LevelService {
    List<Level> getLevelsByJobGroup(JobGroup jobGroup);
    Level getLevelByExp(JobGroup jobGroup, int totalExp);
    Level getNextLevel(JobGroup jobGroup, int totalExp, String currentLevelName);
}
