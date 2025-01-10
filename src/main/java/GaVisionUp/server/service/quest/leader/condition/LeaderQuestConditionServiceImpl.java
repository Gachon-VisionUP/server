package GaVisionUp.server.service.quest.leader.condition;

import GaVisionUp.server.entity.enums.Cycle;
import GaVisionUp.server.entity.enums.Department;
import GaVisionUp.server.entity.quest.leader.LeaderQuestCondition;
import GaVisionUp.server.repository.quest.leader.condition.LeaderQuestConditionRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class LeaderQuestConditionServiceImpl implements LeaderQuestConditionService {

    private final LeaderQuestConditionRepository leaderQuestConditionRepository;

    // ✅ 특정 부서의 퀘스트 조건 목록 조회
    @Override
    public List<LeaderQuestCondition> getConditionsByDepartment(Department department) {
        return leaderQuestConditionRepository.findAllByDepartmentAndCycle(department, Cycle.MONTHLY);
    }

    // ✅ 특정 퀘스트명으로 조회
    @Override
    public LeaderQuestCondition getConditionByQuestName(String questName) {
        return leaderQuestConditionRepository.findByQuestName(questName)
                .orElseThrow(() -> new IllegalArgumentException("해당 퀘스트 조건을 찾을 수 없습니다."));
    }

    // ✅ 새로운 퀘스트 조건 저장
    @Override
    public LeaderQuestCondition saveQuestCondition(Department department, Cycle cycle, String questName, double weight, String maxCondition, String medianCondition, String description) {
        // ✅ 소속별 비중 총합이 100%를 초과하는지 검증
        double totalWeight = leaderQuestConditionRepository.findAllByDepartmentAndCycle(department, cycle)
                .stream()
                .mapToDouble(LeaderQuestCondition::getWeight)
                .sum();

        if (totalWeight + weight > 100.0) {
            throw new IllegalArgumentException("해당 소속의 퀘스트 비중 총합이 100%를 초과할 수 없습니다.");
        }

        // ✅ totalExp 계산 (2000 * weight)
        int totalExp = (int) (2000 * (weight / 100));

        // ✅ Max 및 Median 경험치 계산
        int maxExp = cycle == Cycle.MONTHLY ? totalExp / 12 : totalExp / 52;
        int medianExp = maxExp / 2;

        // ✅ 퀘스트 조건 생성
        LeaderQuestCondition condition = LeaderQuestCondition.builder()
                .department(department)
                .cycle(cycle)
                .questName(questName)
                .weight(weight)
                .totalExp(totalExp)
                .maxExp(maxExp)
                .medianExp(medianExp)
                .maxCondition(maxCondition)
                .medianCondition(medianCondition)
                .description(description)
                .build();

        return leaderQuestConditionRepository.save(condition);
    }
}
