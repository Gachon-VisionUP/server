package GaVisionUp.server.web.dto.quest.team.leader;

import GaVisionUp.server.entity.quest.leader.LeaderQuestCondition;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LeaderQuestConditionResponse {
    private String questName;
    private String cycle;

    public LeaderQuestConditionResponse(LeaderQuestCondition condition) {
        this.questName = condition.getQuestName();
        this.cycle = condition.getCycle().name(); // 월 단위 or 주 단위
    }
}