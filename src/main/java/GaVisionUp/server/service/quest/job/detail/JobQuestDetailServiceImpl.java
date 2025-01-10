package GaVisionUp.server.service.quest.job.detail;

import GaVisionUp.server.entity.enums.Cycle;
import GaVisionUp.server.entity.enums.Department;
import GaVisionUp.server.entity.quest.job.JobQuestDetail;
import GaVisionUp.server.repository.jobquest.job.detail.JobQuestDetailRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class JobQuestDetailServiceImpl implements JobQuestDetailService {

    private final JobQuestDetailRepository jobQuestDetailRepository;

    // ✅ 특정 부서, 직무 그룹, 주기, 회차의 JobQuestDetail 조회
    public Optional<JobQuestDetail> getJobQuestDetail(String department, int part, Cycle cycle, int round) {
        return jobQuestDetailRepository.findByDepartmentAndRound(department, part, cycle, round);
    }

    // ✅ 특정 부서, 직무 그룹, 주기의 모든 JobQuestDetail 조회
    public List<JobQuestDetail> getAllJobQuestDetails(String department, int part, Cycle cycle) {
        return jobQuestDetailRepository.findAllByDepartmentAndCycle(department, part, cycle);
    }

    // ✅ JobQuestDetail 데이터 저장
    public JobQuestDetail saveJobQuestDetail(String department, int part, Cycle cycle, int round, double sales, double laborCost, LocalDate recordedDate) {
        JobQuestDetail jobQuestDetail = JobQuestDetail.create(
                Department.valueOf(department), part, cycle, round, sales, laborCost, recordedDate);
        return jobQuestDetailRepository.save(jobQuestDetail);
    }
}