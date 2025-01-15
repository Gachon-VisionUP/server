package GaVisionUp.server.web.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import java.util.List;

@Getter
@RequiredArgsConstructor
public class HomeResponse {
    private final String userName;  // ✅ 유저 이름
    private final String level;  // ✅ 현재 레벨
    private final int totalExp;  // ✅ 현재 총 경험치
    private final int latestExp;
    private final String imageUrl;
    private final List<HomeQuestInfoResponse> quests; // ✅ 퀘스트 정보 목록
}