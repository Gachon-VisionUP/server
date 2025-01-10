package GaVisionUp.server.web.dto.performance;


import GaVisionUp.server.entity.enums.ExpType;
import GaVisionUp.server.entity.enums.PerformanceGrade;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class PerformanceReviewRequest {
    private Long userId;
    private ExpType expType;
    private PerformanceGrade grade;
}