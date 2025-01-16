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

import java.time.LocalDate;
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
    /*
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

        // ✅ 평가 기준 및 부여 경험치 설정
        double maxCondition = 5.1; // 예시값, DB나 API로부터 동적으로 가져올 수 있음
        double medCondition = 4.3; // 예시값, DB나 API로부터 동적으로 가져올 수 있음
        int maxExp = 80;           // 예시값, 동적으로 설정 가능
        int medExp = 40;           // 예시값, 동적으로 설정 가능

        TeamQuestGrade questGrade;
        int grantedExp;

        if (productivity >= maxCondition) {
            questGrade = TeamQuestGrade.MAX;
            grantedExp = maxExp;
        } else if (productivity >= medCondition) {
            questGrade = TeamQuestGrade.MEDIAN;
            grantedExp = medExp;
        } else {
            questGrade = TeamQuestGrade.MIN;
            grantedExp = 0;
        }

        log.info("📌 [DEBUG] cycle: {}, round: {}, productivity: {}, grade: {}, exp: {}",
                cycle, round, productivity, questGrade, grantedExp);

        JobQuest jobQuest = JobQuest.create(
                Department.valueOf(department), part, cycle, round, productivity,
                maxCondition, medCondition, maxExp, medExp, questGrade, grantedExp
        );
        jobQuestRepository.save(jobQuest);

        List<User> users = userRepository.findByDepartmentAndPart(Department.valueOf(department), part);
        for (User user : users) {
            if (grantedExp > 0) {
                Experience experience = new Experience(user, ExpType.JOB_QUEST, grantedExp);
                experienceRepository.save(experience);
            }
        }
    }
    */

    // ✅ 특정 유저의 소속 & 직무 그룹을 기반으로 연도별 직무 퀘스트 조회
    @Override
    public List<JobQuest> getJobQuestsByYear(Long userId, int year) {
        return jobQuestRepository.findByDepartmentAndPartAndYear(userId, year);
    }

}
