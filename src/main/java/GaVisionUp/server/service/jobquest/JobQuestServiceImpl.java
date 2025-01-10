package GaVisionUp.server.service.jobquest;

import GaVisionUp.server.entity.JobQuest;
import GaVisionUp.server.entity.User;
import GaVisionUp.server.entity.enums.Cycle;
import GaVisionUp.server.entity.enums.Department;
import GaVisionUp.server.entity.enums.ExpType;
import GaVisionUp.server.entity.exp.Experience;
import GaVisionUp.server.repository.exp.experience.ExperienceRepository;
import GaVisionUp.server.repository.jobquest.JobQuestRepository;
import GaVisionUp.server.repository.user.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class JobQuestServiceImpl implements JobQuestService {

    private final JobQuestRepository jobQuestRepository;
    private final UserRepository userRepository;
    private final ExperienceRepository experienceRepository;

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
    public void evaluateJobQuest(String department, int part, String cycle, int round, double productivity) {
        int grantedExp = calculateExp(productivity);

        // ✅ JobQuest 기록 저장
        JobQuest jobQuest = JobQuest.create(Department.valueOf(department), part, Cycle.valueOf(cycle), round, productivity, grantedExp);
        jobQuestRepository.save(jobQuest);

        // ✅ 해당 부서와 직무 그룹에 속한 모든 사용자 조회
        List<User> users = userRepository.findByDepartmentAndPart(Department.valueOf(department), part);
        for (User user : users) {
            // ✅ 경험치 기록 생성 및 저장
            Experience experience = new Experience(user, ExpType.JOB_QUEST, grantedExp);
            experienceRepository.save(experience);
            user.addExperience(grantedExp); // ✅ 유저 총 경험치 반영
        }
    }

    // ✅ 생산성에 따라 경험치 계산
    private int calculateExp(double productivity) {
        if (productivity >= 5.1) {
            return 80; // ✅ Max 등급
        } else if (productivity >= 4.3) {
            return 40; // ✅ Medium 등급
        } else {
            return 0; // ✅ 경험치 없음
        }
    }
}
