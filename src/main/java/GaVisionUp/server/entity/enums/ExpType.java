package GaVisionUp.server.entity.enums;

import GaVisionUp.server.global.exception.RestApiException;
import GaVisionUp.server.global.exception.code.status.GlobalErrorStatus;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Getter
@Slf4j
@RequiredArgsConstructor
public enum ExpType {

    H1_PERFORMANCE("상반기 인사평가"),
    H2_PERFORMANCE("하반기 인사평가"),
    JOB_QUEST("직무별 퀘스트"),
    LEADER_QUEST("리더 부여 퀘스트"),
    WARRIOR_PROJECT("전사 프로젝트");

    @JsonValue
    private final String value;

    @JsonCreator // JSON -> Enum 변환 (역직렬화)
    public static ExpType from(String param) {
        for (ExpType expType : ExpType.values()) {
            if (expType.getValue().equals(param) || expType.name().equals(param)) { // ✅ 한글 값과 Enum name 둘 다 허용
                return expType;
            }
        }
        log.error("ExpType.from() exception occur param: {}", param);
        throw new RestApiException(GlobalErrorStatus._INVALID_EXP_TYPE);
    }
}
