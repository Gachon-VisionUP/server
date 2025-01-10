package GaVisionUp.server.service.quest.leader.condition;

import GaVisionUp.server.entity.enums.Cycle;
import GaVisionUp.server.entity.enums.Department;
import GaVisionUp.server.entity.quest.leader.LeaderQuestCondition;

import java.util.List;

public interface LeaderQuestConditionService {
    List<LeaderQuestCondition> getConditionsByDepartment(Department department);
    LeaderQuestCondition getConditionByQuestName(String questName);
    LeaderQuestCondition saveQuestCondition(Department department, Cycle cycle, String questName, double weight, String maxCondition, String medianCondition, String description);
}
