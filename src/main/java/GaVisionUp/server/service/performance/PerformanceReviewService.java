package GaVisionUp.server.service.performance;

import GaVisionUp.server.entity.PerformanceReview;
import GaVisionUp.server.entity.enums.ExpType;
import GaVisionUp.server.entity.enums.PerformanceGrade;

import java.util.List;

public interface PerformanceReviewService {
    PerformanceReview evaluatePerformance(Long userId, ExpType expType, PerformanceGrade grade);
    List<PerformanceReview> getPerformanceReviewsByUser(Long userId);
    List<PerformanceReview> getAllH1PerformanceReviews();
    List<PerformanceReview> getAllH2PerformanceReviews();
}
