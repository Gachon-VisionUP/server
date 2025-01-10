package GaVisionUp.server.service.jobquest;

import GaVisionUp.server.entity.JobQuest;
import GaVisionUp.server.entity.enums.Department;

import java.util.List;
import java.util.Optional;

public interface JobQuestService {

    // ✅ 특정 부서, 직무 그룹, 주기 및 회차의 JobQuest 조회
    Optional<JobQuest> getJobQuest(String department, int part, String cycle, int round);

    // ✅ 특정 부서, 직무 그룹의 전체 JobQuest 조회
    List<JobQuest> getAllJobQuests(String department, int part, String cycle);

    // ✅ 새로운 JobQuest 저장
    JobQuest createJobQuest(JobQuest jobQuest);
    void evaluateJobQuest(String department, int part, String cycle, int round, double productivity);
}