package GaVisionUp.server.service.quest.job.detail;

import GaVisionUp.server.entity.enums.Cycle;
import GaVisionUp.server.entity.enums.Department;
import GaVisionUp.server.entity.quest.job.JobQuestDetail;
import GaVisionUp.server.repository.quest.job.detail.JobQuestDetailRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class JobQuestDetailServiceImpl implements JobQuestDetailService {

    private final JobQuestDetailRepository jobQuestDetailRepository;

    // ✅ 특정 부서, 직무 그룹, 주기, round의 JobQuestDetail 조회
    @Override
    public List<JobQuestDetail> getJobQuestDetails(String department, int part, Cycle cycle, int round) {
        return jobQuestDetailRepository.findAllByDepartmentAndRound(Department.valueOf(department), part, cycle, round);
    }

    // ✅ 특정 부서, 직무 그룹, 주기의 모든 JobQuestDetail 조회
    @Override
    public List<JobQuestDetail> getAllJobQuestDetails(String department, int part, String cycle) {
        return jobQuestDetailRepository.findAllByDepartmentAndCycle(Department.valueOf(department), part, Cycle.valueOf(cycle));
    }

    // ✅ JobQuestDetail 데이터 저장
    @Override
    public JobQuestDetail saveJobQuestDetail(String department, int part, Cycle cycle, int month, int round, double sales, double laborCost, LocalDate recordedDate) {
        Department deptEnum;
        try {
            deptEnum = Department.valueOf(department);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("잘못된 department 값입니다: " + department);
        }

        JobQuestDetail jobQuestDetail = JobQuestDetail.create(
                deptEnum, part, cycle, month, round, sales, laborCost, recordedDate);
        return jobQuestDetailRepository.save(jobQuestDetail);
    }
}
