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
public enum JobGroup {
    F("현장직군"),
    B("관리직군"),
    G("성장전략"),
    T("기술직군");

    @JsonValue
    private final String value;

    @JsonCreator // Json -> Object, 역직렬화 수행하는 메서드
    public static JobGroup from(String param) {
        for (JobGroup jobGroup : JobGroup.values()) {
            if (jobGroup.getValue().equals(param)) {
                return jobGroup;
            }
        }
        log.error("JobGroup.from() exception occur param: {}", param);
        throw new RestApiException(GlobalErrorStatus._INVALID_JOB_GROUP);
    }
}