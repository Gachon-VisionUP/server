package GaVisionUp.server.web.dto.quest.leader.condition;

import GaVisionUp.server.entity.enums.Cycle;
import GaVisionUp.server.entity.enums.Department;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class LeaderQuestConditionRequest {
    private Department department; // ✅ 소속
    private Cycle cycle; // ✅ 주기 (월간/주간)
    private String questName; // ✅ 퀘스트명
    private double weight; // ✅ 비중 (100% 이내)
    private String maxCondition; // ✅ Max 기준
    private String medianCondition; // ✅ Median 기준
    private String description; // ✅ 설명
}
