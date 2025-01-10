package GaVisionUp.server.repository.quest.leader;

import GaVisionUp.server.entity.enums.Cycle;
import GaVisionUp.server.entity.enums.Department;
import GaVisionUp.server.entity.quest.leader.LeaderQuest;

import java.util.List;

public interface LeaderQuestRepository {
    List<LeaderQuest> findByUserId(Long userId);
    List<LeaderQuest> findAllByDepartmentAndCycle(Department department, Cycle cycle);
    LeaderQuest save(LeaderQuest leaderQuest);
}

