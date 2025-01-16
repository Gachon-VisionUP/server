package GaVisionUp.server.repository.level;

import GaVisionUp.server.entity.Level;
import GaVisionUp.server.entity.enums.JobGroup;

import java.util.List;
import java.util.Optional;

public interface LevelRepository {
    List<Level> findByJobGroup(JobGroup jobGroup);
    Optional<Level> findLevelByExp(JobGroup jobGroup, int totalExp);
    Optional<Level> findByLevelNameAndJobGroup(String levelName, JobGroup jobGroup);
    Optional<Level> findNextLevel(JobGroup jobGroup, int totalExp, String currentLevelName);

    Optional<Level> findById(Long levelId);
    Optional<Level> findByLevelName(String levelName);
    Optional<Level> findByJobGroupAndLevelName(JobGroup jobGroup, String levelName);

    Level save(Level level);
}