package GaVisionUp.server.repository.level;

import GaVisionUp.server.entity.Level;
import GaVisionUp.server.entity.enums.JobGroup;

import java.util.List;
import java.util.Optional;

public interface LevelRepository {
    List<Level> findByJobGroup(JobGroup jobGroup);
    Optional<Level> findLevelByExp(JobGroup jobGroup, int totalExp);
}
