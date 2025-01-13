package GaVisionUp.server.web.dto.quest.team.leader;

import GaVisionUp.server.entity.quest.leader.LeaderQuest;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;

@Data
@AllArgsConstructor
public class LeaderQuestAchievementResponse {
    private String questName;
    private String cycle;
    private String achievementType;
    private int grantedExp;
    private LocalDate assignedDate;

    public LeaderQuestAchievementResponse(LeaderQuest quest) {
        this.questName = quest.getQuestName();
        this.cycle = quest.getCycle().name();
        this.achievementType = quest.getAchievementType();
        this.grantedExp = quest.getGrantedExp();
        this.assignedDate = quest.getAssignedDate();
    }
}
