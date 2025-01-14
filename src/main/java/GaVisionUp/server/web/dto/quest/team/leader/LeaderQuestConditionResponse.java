package GaVisionUp.server.web.dto.quest.team.leader;

import GaVisionUp.server.entity.quest.leader.LeaderQuestCondition;
import lombok.AllArgsConstructor;
import lombok.Data;

// ✅ 퀘스트 조건 응답 DTO (상단 목록)
@Data
@AllArgsConstructor
public class LeaderQuestConditionResponse {
    private String questName;
    private String cycle;

    public LeaderQuestConditionResponse(LeaderQuestCondition condition) {
        this.questName = condition.getQuestName();
        this.cycle = condition.getCycle().name();
    }
}
