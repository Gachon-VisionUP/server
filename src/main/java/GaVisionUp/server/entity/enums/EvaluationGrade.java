package GaVisionUp.server.entity.enums;

import lombok.Getter;

@Getter
public enum EvaluationGrade {
    S(6500),
    A(4500),
    B(3000),
    C(1500),
    D(0);

    private final int exp;

    EvaluationGrade(int exp) {
        this.exp = exp;
    }
}