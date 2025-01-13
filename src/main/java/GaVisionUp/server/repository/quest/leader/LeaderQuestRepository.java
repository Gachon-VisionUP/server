package GaVisionUp.server.repository.quest.leader;

import GaVisionUp.server.entity.enums.Cycle;
import GaVisionUp.server.entity.enums.Department;
import GaVisionUp.server.entity.quest.leader.LeaderQuest;

import java.util.List;
import java.util.Optional;

public interface LeaderQuestRepository {
    List<LeaderQuest> findByUserId(Long userId);
    List<LeaderQuest> findAllByDepartmentAndCycle(Department department, Cycle cycle);
    Optional<LeaderQuest> findByDepartmentAndCycleAndRound(String department, Cycle cycle, int round);
    Optional<LeaderQuest> findByDepartmentAndCycleAndMonth(String department, Cycle cycle, int month);
    LeaderQuest save(LeaderQuest leaderQuest);
    Optional<LeaderQuest> findById(Long id);
    List<LeaderQuest> findByUserIdAndYear(Long userId, int year);
}

