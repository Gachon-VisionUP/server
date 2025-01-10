package GaVisionUp.server.web.controller.performance;

import GaVisionUp.server.entity.PerformanceReview;
import GaVisionUp.server.service.performance.PerformanceReviewService;
import GaVisionUp.server.web.dto.performance.PerformanceReviewRequest;
import GaVisionUp.server.web.dto.performance.PerformanceReviewResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/performance-review")
@RequiredArgsConstructor
public class PerformanceReviewController {

    private final PerformanceReviewService performanceReviewService;

    @PostMapping("/evaluate")
    public ResponseEntity<PerformanceReviewResponse> evaluatePerformance(
            @RequestBody PerformanceReviewRequest request) {  // ✅ JSON 본문을 받을 수 있도록 수정
        PerformanceReview review = performanceReviewService.evaluatePerformance(
                request.getUserId(), request.getExpType(), request.getGrade());
        return ResponseEntity.ok(new PerformanceReviewResponse(review));
    }

    // ✅ 특정 유저의 인사평가 조회 (DTO 변환)
    @GetMapping("/{userId}")
    public ResponseEntity<List<PerformanceReviewResponse>> getUserReviews(@PathVariable Long userId) {
        List<PerformanceReviewResponse> responses = performanceReviewService.getPerformanceReviewsByUser(userId)
                .stream()
                .map(PerformanceReviewResponse::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

    // ✅ 전체 유저의 상반기 인사평가 조회 (DTO 변환)
    @GetMapping("/h1")
    public ResponseEntity<List<PerformanceReviewResponse>> getH1Reviews() {
        List<PerformanceReviewResponse> responses = performanceReviewService.getAllH1PerformanceReviews()
                .stream()
                .map(PerformanceReviewResponse::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

    // ✅ 전체 유저의 하반기 인사평가 조회 (DTO 변환)
    @GetMapping("/h2")
    public ResponseEntity<List<PerformanceReviewResponse>> getH2Reviews() {
        List<PerformanceReviewResponse> responses = performanceReviewService.getAllH2PerformanceReviews()
                .stream()
                .map(PerformanceReviewResponse::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }
}