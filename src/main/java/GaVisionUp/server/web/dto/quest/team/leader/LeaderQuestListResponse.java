package GaVisionUp.server.web.dto.quest.team.leader;


import GaVisionUp.server.entity.quest.leader.LeaderQuest;
import GaVisionUp.server.entity.quest.leader.LeaderQuestCondition;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class LeaderQuestListResponse {
    private List<LeaderQuestConditionResponse> conditions; // ✅ 퀘스트 목록 (조건)
    private List<LeaderQuestAchievementResponse> achievements; // ✅ 퀘스트 달성 목록
}

