package GaVisionUp.server.web.dto.quest.team.leader;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;
import java.util.Map;


@Data
@AllArgsConstructor
public class LeaderQuestListResponse {
    private List<LeaderQuestConditionResponse> conditions;  // ✅ 퀘스트 조건 (상단)
    private Map<String, List<LeaderQuestAchievementResponse>> achievements;  // ✅ 퀘스트 명별로 그룹화된 데이터
}
