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
public enum Role {
    USER("사용자"),
    ADMIN("관리자");

    @JsonValue
    private final String value;

    @JsonCreator // Json -> Object, 역직렬화 수행하는 메서드
    public static Role from(String param) {
        for (Role role : Role.values()) {
            if (role.getValue().equals(param)) {
                return role;
            }
        }
        log.error("Role.from() exception occur param: {}", param);
        throw new RestApiException(GlobalErrorStatus._INVALID_ROLE);
    }
}
