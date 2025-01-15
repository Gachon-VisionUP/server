package GaVisionUp.server.web.dto;

import lombok.Getter;

@Getter
public class HomeQuestInfoResponse {
    private final String questName;  // ✅ 퀘스트명
    private final Integer grantedExp; // ✅ 부여된 경험치 (없으면 null)

    public HomeQuestInfoResponse(String questName, Integer grantedExp) {
        this.questName = questName;
        this.grantedExp = grantedExp;
    }
}