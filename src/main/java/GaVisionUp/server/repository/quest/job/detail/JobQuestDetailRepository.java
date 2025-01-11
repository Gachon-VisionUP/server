package GaVisionUp.server.repository.quest.job.detail;

import GaVisionUp.server.entity.enums.Cycle;
import GaVisionUp.server.entity.enums.Department;
import GaVisionUp.server.entity.quest.job.JobQuestDetail;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JobQuestDetailRepository {
    List<JobQuestDetail> findAllByDepartmentAndRound(Department department, int part, Cycle cycle, int round);
    List<JobQuestDetail> findAllByDepartmentAndCycle(Department department, int part, Cycle cycle);
    JobQuestDetail save(JobQuestDetail jobQuestDetail);
}