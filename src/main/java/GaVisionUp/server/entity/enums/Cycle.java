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
public enum Cycle {
    MONTHLY("월"),
    WEEKLY("주");

    @JsonValue
    private final String value;

    @JsonCreator // Json -> Object, 역직렬화 수행하는 메서드
    public static Cycle from(String param) {
        for (Cycle cycle : Cycle.values()) {
            if (cycle.getValue().equals(param) || cycle.name().equals(param)) { // ✅ 한글 값과 Enum name 둘 다 허용
                return cycle;
            }
        }
        log.error("Cycle.from() exception occur param: {}", param);
        throw new RestApiException(GlobalErrorStatus._INVALID_CYCLE);
    }
}