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
public enum Filter {
    LATEST("최신순"),
    OLDEST("오래된순");

    @JsonValue
    private final String value;

    @JsonCreator // Json -> Object, 역직렬화 수행하는 메서드
    public static Filter from(String param) {
        System.out.println("이거 되나???");
        for (Filter filter : Filter.values()) {
            if (filter.getValue().equals(param) || filter.name().equals(param)) { // ✅ 한글 값과 Enum name 둘 다 허용
                return filter;
            }
        }
        log.error("Filter.from() exception occur param: {}", param);
        throw new RestApiException(GlobalErrorStatus._INVALID_FILTER);
    }
}