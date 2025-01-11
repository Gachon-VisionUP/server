package GaVisionUp.server.repository.quest.job;

import GaVisionUp.server.entity.enums.Cycle;
import GaVisionUp.server.entity.quest.job.JobQuest;

import java.util.List;
import java.util.Optional;

public interface JobQuestRepository {
    Optional<JobQuest> findByDepartmentAndCycleAndRound(String department, int part, Cycle cycle, int round);
    Optional<JobQuest> findByDepartmentAndCycleAndMonth(String department, int part, Cycle cycle, int month);
    List<JobQuest> findAllByDepartment(String department, int part, Cycle cycle);
    JobQuest save(JobQuest jobQuest);
}