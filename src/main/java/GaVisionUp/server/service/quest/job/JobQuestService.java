package GaVisionUp.server.service.quest.job;

import GaVisionUp.server.entity.enums.Cycle;
import GaVisionUp.server.entity.quest.job.JobQuest;

import java.util.List;
import java.util.Optional;

public interface JobQuestService {

    // ✅ 특정 부서, 직무 그룹, 주기 및 회차의 JobQuest 조회
    Optional<JobQuest> getJobQuest(String department, int jobGroup, Cycle cycle, int round);

    // ✅ 특정 부서, 직무 그룹의 전체 JobQuest 조회
    List<JobQuest> getAllJobQuests(String department, int jobGroup, String cycle);
    // ✅ 새로운 JobQuest 저장
    JobQuest createJobQuest(JobQuest jobQuest);
    void evaluateJobQuest(String department, int part, Cycle cycle, int round);
    List<JobQuest> getJobQuestsByYear(Long userId, int year);
}