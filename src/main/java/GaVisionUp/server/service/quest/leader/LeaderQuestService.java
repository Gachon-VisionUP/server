package GaVisionUp.server.service.quest.leader;

import GaVisionUp.server.entity.enums.Cycle;
import GaVisionUp.server.entity.enums.Department;
import GaVisionUp.server.entity.quest.leader.LeaderQuest;

import java.time.LocalDate;
import java.util.List;

public interface LeaderQuestService {
    List<LeaderQuest> getLeaderQuests(Long userId);
    List<LeaderQuest> getAllLeaderQuestsByDepartment(Department department);
    LeaderQuest assignLeaderQuest(Long userId, Cycle cycle, String questName, int month, Integer week, String achievementType, String note, LocalDate assignedDate);
}
