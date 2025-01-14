package GaVisionUp.server.web.dto.quest.team.leader;

import GaVisionUp.server.entity.quest.leader.LeaderQuest;
import lombok.AllArgsConstructor;
import lombok.Data;

// ✅ 퀘스트 달성 데이터 응답 DTO (하단 미리보기)
@Data
@AllArgsConstructor
public class LeaderQuestAchievementResponse {
    private String questName; // ✅ 퀘스트 명 (그룹화 키)
    private Long questId;     // ✅ 개별 퀘스트 ID 추가 (상세 정보용)
    private String cycle;     // ✅ 주기 (MONTHLY / WEEKLY)
    private int month;        // ✅ 월 (MONTHLY 전용)
    private Integer week;     // ✅ 주차 (WEEKLY 전용, Nullable)
    private String teamGrade; // ✅ 달성 등급
    private int grantedExp;   // ✅ 부여된 경험치 추가

    public LeaderQuestAchievementResponse(LeaderQuest quest) {
        this.questName = quest.getQuestName();
        this.questId = quest.getId();             // ✅ 상세 정보용 ID 추가
        this.cycle = quest.getCycle().name();
        this.month = quest.getMonth();
        this.week = quest.getWeek();
        this.teamGrade = quest.getAchievementType();
        this.grantedExp = quest.getGrantedExp();  // ✅ 경험치 추가
    }

    // ✅ 퀘스트 명 반환 (그룹화에 사용)
    public String getQuestName() {
        return questName;
    }
}
