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
public enum Department {
    EUMSEONG1("음성 1센터"),
    EUMSEONG2("음성 2센터"),
    BAEGAM("용인백암센터"),
    NAMYANGJU("남양주센터"),
    PAJU("파주센터"),
    BUSINESS("사업기획팀"),
    GROSS("그로스팀"),
    CX("CX팀"),
    ;

    @JsonValue
    private final String value;

    @JsonCreator // Json -> Object, 역직렬화 수행하는 메서드
    public static Department from(String param) {
        for (Department department : Department.values()) {
            if (department.getValue().equals(param)) {
                return department;
            }
        }
        log.error("department.from() exception occur param: {}", param);
        throw new RestApiException(GlobalErrorStatus._INVALID_DEPARTMENT);
    }
}