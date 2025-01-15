package GaVisionUp.server.repository.performance;

import GaVisionUp.server.entity.PerformanceReview;
import GaVisionUp.server.entity.enums.ExpType;

import java.util.List;
import java.util.Optional;



public interface PerformanceReviewRepository {

    // ✅ 인사평가 저장
    PerformanceReview save(PerformanceReview performanceReview);

    // ✅ 특정 ID로 인사평가 조회
    Optional<PerformanceReview> findById(Long id);

    // ✅ 특정 사용자의 모든 인사평가 조회
    List<PerformanceReview> findByUserId(Long userId);

    // ✅ 특정 사용자의 특정 유형(H1Performance, H2Performance)의 인사평가 조회
    List<PerformanceReview> findByUserIdAndExpType(Long userId, ExpType expType);

    // ✅ 특정 사용자의 최신 인사평가 조회
    Optional<PerformanceReview> findLatestPerformanceReview(Long userId, ExpType expType);

    // ✅ 전체 유저의 상반기(H1_PERFORMANCE) 인사평가 조회
    List<PerformanceReview> findAllByH1Performance();

    // ✅ 전체 유저의 하반기(H2_PERFORMANCE) 인사평가 조회
    List<PerformanceReview> findAllByH2Performance();

    Optional<PerformanceReview>findByUserIdAndYearAndExpType(Long userId,int year,ExpType expType);
}