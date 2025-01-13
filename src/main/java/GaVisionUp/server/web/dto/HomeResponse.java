package GaVisionUp.server.web.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class HomeResponse {
    private final int latestExp;  // ✅ 가장 최신 획득 경험치
    private final int totalExp;   // ✅ 총 경험치
}