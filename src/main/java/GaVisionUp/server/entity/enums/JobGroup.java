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

    @JsonCreator
    public static JobGroup from(String param) {
        for (JobGroup jobGroup : JobGroup.values()) {
            // 열거형 이름 또는 value 둘 다 매칭 가능하도록 추가
            if (jobGroup.name().equalsIgnoreCase(param.trim()) || jobGroup.getValue().equals(param.trim())) {
                return jobGroup;
            }
        }
        log.error("JobGroup.from() exception occur param: {}", param);
        throw new RestApiException(GlobalErrorStatus._INVALID_JOB_GROUP);
    }
}