package GaVisionUp.server.web.dto.quest.team.leader.detail;

import GaVisionUp.server.entity.quest.leader.LeaderQuest;
import GaVisionUp.server.entity.quest.leader.LeaderQuestCondition;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class LeaderQuestDetailResponse {
    private String questName;
    private String cycle;
    private String maxCondition; // MAX 등급 달성 조건
    private String medCondition; // MED 등급 달성 조건
    private List<LeaderQuestInstanceResponse> questInstances; // ✅ 동일한 퀘스트 ID를 가진 모든 리더 퀘스트 목록

    public LeaderQuestDetailResponse(LeaderQuestCondition condition, List<LeaderQuest> quests) {
        this.questName = condition.getQuestName();
        this.cycle = condition.getCycle().name();
        this.maxCondition = condition.getMaxCondition();
        this.medCondition = condition.getMedianCondition();
        this.questInstances = quests.stream().map(LeaderQuestInstanceResponse::new).toList();
    }
}

// ✅ 개별 리더 퀘스트 데이터
@Data
@AllArgsConstructor
class LeaderQuestInstanceResponse {
    private String achievementType; // 달성 등급 (Max, Med)
    private int grantedExp; // 부여된 경험치
    private String note; // 비고 (description)
    private int month; // 월 (월간 퀘스트일 경우)
    private Integer week; // 주차 (주간 퀘스트일 경우)

    public LeaderQuestInstanceResponse(LeaderQuest quest) {
        this.achievementType = quest.getAchievementType();
        this.grantedExp = quest.getGrantedExp();
        this.note = quest.getNote();
        this.month = quest.getMonth();
        this.week = quest.getWeek();
    }
}
