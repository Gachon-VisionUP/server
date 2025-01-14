package GaVisionUp.server.web.dto.quest.team.leader;

import GaVisionUp.server.entity.quest.leader.LeaderQuest;
import lombok.AllArgsConstructor;
import lombok.Data;

// ✅ 퀘스트 달성 데이터 응답 DTO (하단 미리보기)
@Data
@AllArgsConstructor
public class LeaderQuestAchievementResponse {
    private String questName;
    private int month; // ✅ 월별 퀘스트의 경우
    private Integer week; // ✅ 주별 퀘스트의 경우 (nullable)
    private String teamGrade; // ✅ 달성 등급

    public LeaderQuestAchievementResponse(LeaderQuest quest) {
        this.questName = quest.getQuestName();
        this.month = quest.getMonth();
        this.week = quest.getWeek();
        this.teamGrade = quest.getAchievementType();
    }
}
