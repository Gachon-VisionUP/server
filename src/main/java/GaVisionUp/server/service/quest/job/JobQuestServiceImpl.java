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

    // ✅ 특정 부서, 직무 그룹, 주기 및 회차의 JobQuest 조회
    @Override
    public Optional<JobQuest> getJobQuest(String department, int jobGroup, String cycle, int round) {
        return jobQuestRepository.findByDepartmentAndRound(department, jobGroup, cycle, round);
    }

    // ✅ 특정 부서, 직무 그룹의 전체 JobQuest 조회
    @Override
    public List<JobQuest> getAllJobQuests(String department, int jobGroup, String cycle) {
        return jobQuestRepository.findAllByDepartment(department, jobGroup, cycle);
    }

    // ✅ 새로운 JobQuest 저장
    @Override
    public JobQuest createJobQuest(JobQuest jobQuest) {
        return jobQuestRepository.save(jobQuest);
    }

    // ✅ 직무별 퀘스트 점수 평가 및 경험치 부여
    @Override
    public void evaluateJobQuest(String department, int part, Cycle cycle, int month, Integer week) {
        List<JobQuestDetail> details = jobQuestDetailRepository.findAllByDepartmentAndMonthAndWeek(
                Department.valueOf(department), part, cycle, month, week
        );

        if (details.isEmpty()) {
            throw new IllegalArgumentException("해당 주차의 직무별 퀘스트 상세 데이터가 존재하지 않습니다.");
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

        // ✅ CYCLE에 따른 round 계산
        int round = calculateRound(cycle, month, week);

        log.info("📌 [DEBUG] cycle: {}, month: {}, week: {}, round: {}", cycle, month, week, round);

        // ✅ JobQuest 기록 저장
        JobQuest jobQuest = JobQuest.create(
                Department.valueOf(department), part, cycle, round, month, week, productivity, questGrade, grantedExp
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

    // ✅ CYCLE에 따라 round 계산 방식 변경
    private int calculateRound(Cycle cycle, int month, Integer week) {
        if (cycle == Cycle.MONTHLY) {
            return month; // ✅ 월 단위이면 round는 month와 동일
        } else if (cycle == Cycle.WEEKLY) {
            if (week == null) {
                throw new IllegalArgumentException("주 단위 평가에서는 week 값이 필요합니다.");
            }
            return (month - 1) * 5 + week; // ✅ 주 단위이면 (월 - 1) * 5 + 주차
        } else {
            throw new IllegalArgumentException("지원하지 않는 cycle 값입니다: " + cycle);
        }
    }
}
