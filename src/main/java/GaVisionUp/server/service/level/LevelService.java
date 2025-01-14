package GaVisionUp.server.service.level;

import GaVisionUp.server.entity.Level;
import GaVisionUp.server.entity.enums.JobGroup;

import java.util.List;
import java.util.Optional;

public interface LevelService {
    List<Level> getLevelsByJobGroup(JobGroup jobGroup);
    Level getLevelByExp(JobGroup jobGroup, int totalExp);
    Level getNextLevel(JobGroup jobGroup, int totalExp, String currentLevelName);
    Level getLevelByNameAndJobGroup(String levelName, JobGroup jobGroup);
    Optional<Level> findNextLevel(JobGroup jobGroup, int totalExp, String currentLevelName);

}
