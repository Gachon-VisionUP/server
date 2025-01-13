package GaVisionUp.server.web.dto.quest.team;

import GaVisionUp.server.entity.enums.Cycle;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class TeamQuestJobYearlyResponse {
    private Cycle cycle; // ✅ 주기 (주 or 월)
    private List<TeamQuestJobResponse> quests; // ✅ 직무별 퀘스트 평가 정보
}
