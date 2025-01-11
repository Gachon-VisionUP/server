package GaVisionUp.server.service.quest.job;

import GaVisionUp.server.entity.enums.TeamQuestGrade;
import GaVisionUp.server.entity.quest.job.JobQuest;
import GaVisionUp.server.entity.User;
import GaVisionUp.server.entity.enums.Cycle;
import GaVisionUp.server.entity.enums.Department;
import GaVisionUp.server.entity.enums.ExpType;
import GaVisionUp.server.entity.exp.Experience;
import GaVisionUp.server.entity.quest.job.JobQuestDetail;
import GaVisionUp.server.repository.exp.experience.ExperienceRepository;
import GaVisionUp.server.repository.quest.job.JobQuestRepository;
import GaVisionUp.server.repository.quest.job.detail.JobQuestDetailRepository;
import GaVisionUp.server.repository.user.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class JobQuestServiceImpl implements JobQuestService {

    private final JobQuestRepository jobQuestRepository;
    private final UserRepository userRepository;
    private final ExperienceRepository experienceRepository;
    private final JobQuestDetailRepository jobQuestDetailRepository;

    // ✅ 특정 부서, 직무 그룹, 주기 및 round 값으로 JobQuest 조회
    @Override
    public Optional<JobQuest> getJobQuest(String department, int jobGroup, Cycle cycle, int round) {
        return jobQuestRepository.findByDepartmentAndCycleAndRound(department, jobGroup, cycle, round);
    }

    // ✅ 특정 부서, 직무 그룹의 전체 JobQuest 조회
    @Override
    public List<JobQuest> getAllJobQuests(String department, int jobGroup, String cycle) {
        return jobQuestRepository.findAllByDepartment(department, jobGroup, Cycle.valueOf(cycle));
    }

    // ✅ 새로운 JobQuest 저장
    @Override
    public JobQuest createJobQuest(JobQuest jobQuest) {
        return jobQuestRepository.save(jobQuest);
    }

    // ✅ 직무별 퀘스트 점수 평가 및 경험치 부여 (round 직접 입력)
    @Override
    public void evaluateJobQuest(String department, int part, Cycle cycle, int round) {
        List<JobQuestDetail> details = jobQuestDetailRepository.findAllByDepartmentAndCycleAndRound(
                Department.valueOf(department), part, cycle, round);

        if (details.isEmpty()) {
            throw new IllegalArgumentException("해당 round의 직무별 퀘스트 상세 데이터가 존재하지 않습니다.");
        }

        double totalSales = details.stream().mapToDouble(JobQuestDetail::getSales).sum();
        double totalLaborCost = details.stream().mapToDouble(JobQuestDetail::getLaborCost).sum();
        double productivity = (totalLaborCost == 0) ? 0.0 : totalSales / totalLaborCost;

        TeamQuestGrade questGrade;
        int grantedExp;

        if (productivity >= 5.1) {
            grantedExp = 80;
            questGrade = TeamQuestGrade.MAX;
        } else if (productivity >= 4.3) {
            grantedExp = 40;
            questGrade = TeamQuestGrade.MEDIAN;
        } else {
            grantedExp = 0;
            questGrade = TeamQuestGrade.MIN;
        }

        log.info("📌 [DEBUG] cycle: {}, round: {}", cycle, round);

        JobQuest jobQuest = JobQuest.create(
                Department.valueOf(department), part, cycle, round, productivity, questGrade, grantedExp
        );
        jobQuestRepository.save(jobQuest);

        List<User> users = userRepository.findByDepartmentAndPart(Department.valueOf(department), part);
        for (User user : users) {
            if (questGrade != TeamQuestGrade.MIN) {
                Experience experience = new Experience(user, ExpType.JOB_QUEST, grantedExp);
                experienceRepository.save(experience);
            }
        }
    }
}
