package GaVisionUp.server.repository.jobquest;

import GaVisionUp.server.entity.JobQuest;

import java.util.List;
import java.util.Optional;

public interface JobQuestRepository {

    // ✅ 특정 부서, 직무 그룹, 주기 및 회차로 조회
    Optional<JobQuest> findByDepartmentAndRound(String department, int part, String cycle, int round);

    // ✅ 특정 부서, 직무 그룹의 전체 기록 조회 (주기별 정렬)
    List<JobQuest> findAllByDepartment(String department, int part, String cycle);

    // ✅ 경험치 부여 기록 저장
    JobQuest save(JobQuest jobQuest);
}