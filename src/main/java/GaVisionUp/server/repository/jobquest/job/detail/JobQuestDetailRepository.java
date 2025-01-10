package GaVisionUp.server.repository.jobquest.job.detail;

import GaVisionUp.server.entity.enums.Cycle;
import GaVisionUp.server.entity.quest.job.JobQuestDetail;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface JobQuestDetailRepository {

    Optional<JobQuestDetail> findByDepartmentAndRound(String department, int part, Cycle cycle, int round);

    List<JobQuestDetail> findAllByDepartmentAndCycle(String department, int part, Cycle cycle);

    JobQuestDetail save(JobQuestDetail jobQuestDetail);
}