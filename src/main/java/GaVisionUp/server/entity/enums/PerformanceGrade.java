package GaVisionUp.server.entity.enums;

import lombok.Getter;

@Getter
public enum PerformanceGrade {
    S(6500),
    A(4500),
    B(3000),
    C(1500),
    D(0);

    private final int exp;

    PerformanceGrade(int exp) {
        this.exp = exp;
    }

    /**
     * ✅ 문자열을 PerformanceGrade Enum으로 변환
     */
    public static PerformanceGrade fromString(String grade) {
        try {
            return PerformanceGrade.valueOf(grade.toUpperCase()); // ✅ 대소문자 구분 없이 변환
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("⚠️ [ERROR] 유효하지 않은 인사평가 등급: " + grade);
        }
    }
}