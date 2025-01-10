package GaVisionUp.server.web.dto.performance;

import GaVisionUp.server.entity.PerformanceReview;
import GaVisionUp.server.entity.enums.ExpType;
import GaVisionUp.server.entity.enums.PerformanceGrade;
import lombok.Getter;

import java.time.LocalDate;

@Getter
public class PerformanceReviewResponse {
    private final Long id;
    private final Long userId;
    private final String userName;
    private final ExpType expType;
    private final PerformanceGrade grade;
    private final int grantedExp;
    private final LocalDate evaluationDate;

    public PerformanceReviewResponse(PerformanceReview review) {
        this.id = review.getId();
        this.userId = review.getUser().getId();
        this.userName = review.getUser().getName();
        this.expType = review.getExpType();
        this.grade = review.getGrade();
        this.grantedExp = review.getGrantedExp();
        this.evaluationDate = review.getEvaluationDate();
    }
}
