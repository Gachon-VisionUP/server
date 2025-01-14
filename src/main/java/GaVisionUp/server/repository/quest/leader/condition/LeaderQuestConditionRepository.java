package GaVisionUp.server.repository.quest.leader.condition;

import GaVisionUp.server.entity.enums.Cycle;
import GaVisionUp.server.entity.enums.Department;
import GaVisionUp.server.entity.quest.leader.LeaderQuestCondition;

import java.util.List;
import java.util.Optional;

public interface LeaderQuestConditionRepository {
    List<LeaderQuestCondition> findAllByDepartmentAndCycle(Department department, Cycle cycle);
    Optional<LeaderQuestCondition> findByQuestName(String questName);
    LeaderQuestCondition save(LeaderQuestCondition questCondition);
    List<LeaderQuestCondition> findByDepartment(Department department);
    Optional<LeaderQuestCondition> findById(Long id);
}
