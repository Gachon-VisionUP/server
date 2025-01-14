package GaVisionUp.server.repository.quest.leader;

import GaVisionUp.server.entity.enums.Cycle;
import GaVisionUp.server.entity.enums.Department;
import GaVisionUp.server.entity.quest.leader.LeaderQuest;

import java.util.List;
import java.util.Optional;

public interface LeaderQuestRepository {
    List<LeaderQuest> findByUserId(Long userId);
    List<LeaderQuest> findAllByDepartmentAndCycle(Department department, Cycle cycle);
    LeaderQuest save(LeaderQuest leaderQuest);
    Optional<LeaderQuest> findById(Long id);
    Optional<LeaderQuest> findByUserIdAndQuestId(Long userId, Long questId);
    List<LeaderQuest> findByUserIdAndConditionId(Long userId, Long conditionId); // ✅ 추가됨
    List<LeaderQuest> findByUserIdAndYear(Long userId, int year);
}

