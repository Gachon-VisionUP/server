package GaVisionUp.server.entity.enums;

import lombok.Getter;

@Getter
public enum JobQuestGrade {
    MAX(4000),
    MEDIAN(2000),
    MIN(0);

    private final int exp;

    JobQuestGrade(int exp) {
        this.exp = exp;
    }
}