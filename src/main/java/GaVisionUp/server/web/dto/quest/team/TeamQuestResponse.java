package GaVisionUp.server.web.dto.quest.team;

import GaVisionUp.server.entity.enums.TeamQuestGrade;
import GaVisionUp.server.entity.quest.TeamQuest;
import lombok.Getter;

import java.time.LocalDate;

@Getter
public class TeamQuestResponse {
    private Long userId;
    private int month;
    private int week;
    private int day;
    private LocalDate recordedDate;
    private TeamQuestGrade questGrade;

    public TeamQuestResponse(TeamQuest teamQuest) {
        this.userId = teamQuest.getUser().getId();
        this.month = teamQuest.getMonth();
        this.week = teamQuest.getWeek();
        this.day = teamQuest.getDay();
        this.recordedDate = teamQuest.getRecordedDate();
        this.questGrade = teamQuest.getQuestGrade();
    }
}
