package GaVisionUp.server.web.dto.quest.team.leader;


import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class LeaderQuestListResponse {
    private List<LeaderQuestConditionResponse> conditions; // ✅ 퀘스트 조건 목록
    private List<LeaderQuestAchievementResponse> achievements; // ✅ 퀘스트 달성 등급
}