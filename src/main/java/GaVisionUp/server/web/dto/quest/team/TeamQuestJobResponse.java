package GaVisionUp.server.web.dto.quest.team;

import GaVisionUp.server.entity.enums.TeamQuestGrade;
import GaVisionUp.server.entity.quest.job.JobQuest;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;

@Data
@AllArgsConstructor
public class TeamQuestJobResponse {
    private int round; // ✅ round 값
    private TeamQuestGrade grade; // ✅ 평가 등급
    private int grantedExp; // ✅ 부여된 경험치
    private LocalDate grantedDate; // ✅ 평가 날짜

    public TeamQuestJobResponse(JobQuest jobQuest) {
        this.round = jobQuest.getRound();
        this.grade = jobQuest.getQuestGrade();
        this.grantedExp = jobQuest.getGrantedExp();
        this.grantedDate = jobQuest.getGrantedDate();
    }
}
