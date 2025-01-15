package GaVisionUp.server.repository.quest.job.detail;

import GaVisionUp.server.entity.enums.Cycle;
import GaVisionUp.server.entity.enums.Department;
import GaVisionUp.server.entity.quest.job.JobQuestDetail;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface JobQuestDetailRepository {
    List<JobQuestDetail> findAllByDepartmentAndCycleAndRound(Department department, int part, Cycle cycle, int round);
    List<JobQuestDetail> findAllByDepartmentAndCycle(Department department, int part, Cycle cycle);
    JobQuestDetail save(JobQuestDetail jobQuestDetail);
    Optional<JobQuestDetail> findByRecordedDate(LocalDate recordedDate); // ✅ 특정 날짜 기준 조회
    List<JobQuestDetail> findAllJobQuests();
}