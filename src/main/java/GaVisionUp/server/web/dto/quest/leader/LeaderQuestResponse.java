package GaVisionUp.server.web.dto.quest.leader;

import GaVisionUp.server.entity.quest.leader.LeaderQuest;
import GaVisionUp.server.web.dto.quest.team.leader.LeaderQuestConditionResponse;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;

@Data
@AllArgsConstructor
public class LeaderQuestResponse {
    private Long id;
    private String questName;
    private String cycle;
    private int month;
    private Integer week;
    private String achievementType;
    private int grantedExp;
    private String note;
    private LocalDate assignedDate;
    private LeaderQuestConditionResponse condition; // ✅ 조건 정보 추가

    public LeaderQuestResponse(LeaderQuest quest) {
        this.id = quest.getId();
        this.questName = quest.getQuestName();
        this.cycle = quest.getCycle().name();
        this.month = quest.getMonth();
        this.week = quest.getWeek();
        this.achievementType = quest.getAchievementType();
        this.grantedExp = quest.getGrantedExp();
        this.note = quest.getNote();
        this.assignedDate = quest.getAssignedDate();
        this.condition = new LeaderQuestConditionResponse(quest.getCondition()); // ✅ 조건 정보 매핑
    }
}
