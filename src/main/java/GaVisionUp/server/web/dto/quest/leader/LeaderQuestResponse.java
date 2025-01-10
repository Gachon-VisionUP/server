package GaVisionUp.server.web.dto.quest.leader;

import GaVisionUp.server.entity.quest.leader.LeaderQuest;
import lombok.Getter;

import java.time.LocalDate;

@Getter
public class LeaderQuestResponse {
    private final Long id;
    private final Long userId;
    private final String department;
    private final String cycle;
    private final String questName;
    private final int month;
    private final Integer week;
    private final String achievementType;
    private final int grantedExp;
    private final String note;
    private final LocalDate assignedDate;

    public LeaderQuestResponse(LeaderQuest leaderQuest) {
        this.id = leaderQuest.getId();
        this.userId = leaderQuest.getUser().getId();
        this.department = leaderQuest.getDepartment().name();
        this.cycle = leaderQuest.getCycle().name();
        this.questName = leaderQuest.getQuestName();
        this.month = leaderQuest.getMonth();
        this.week = leaderQuest.getWeek();
        this.achievementType = leaderQuest.getAchievementType();
        this.grantedExp = leaderQuest.getGrantedExp();
        this.note = leaderQuest.getNote();
        this.assignedDate = leaderQuest.getAssignedDate();
    }
}
