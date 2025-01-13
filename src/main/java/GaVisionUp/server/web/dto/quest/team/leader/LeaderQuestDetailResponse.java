package GaVisionUp.server.web.dto.quest.team.leader;

import GaVisionUp.server.entity.quest.leader.LeaderQuest;
import GaVisionUp.server.entity.quest.leader.LeaderQuestCondition;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LeaderQuestDetailResponse {
    private String questName;
    private String cycle;
    private String maxCondition; // MAX 등급 달성 조건
    private String medCondition; // MED 등급 달성 조건
    private String achievementType; // 달성 등급
    private int grantedExp; // 부여된 경험치
    private String description; // 비고

    public LeaderQuestDetailResponse(LeaderQuest quest, LeaderQuestCondition condition) {
        this.questName = quest.getQuestName();
        this.cycle = quest.getCycle().name();
        this.maxCondition = condition.getMaxCondition();
        this.medCondition = condition.getMedianCondition();
        this.achievementType = quest.getAchievementType();
        this.grantedExp = quest.getGrantedExp();
        this.description = condition.getDescription();
    }
}
