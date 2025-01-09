package GaVisionUp.server.entity.enums;

import lombok.Getter;

@Getter
public enum LeaderQuestGrade {
    MAX(2000),
    MEDIAN(1000),
    MIN(0);

    private final int exp;

    LeaderQuestGrade(int exp) {
        this.exp = exp;
    }
}