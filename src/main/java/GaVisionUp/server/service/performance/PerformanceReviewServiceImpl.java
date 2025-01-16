package GaVisionUp.server.service.performance;

import GaVisionUp.server.entity.PerformanceReview;
import GaVisionUp.server.entity.User;
import GaVisionUp.server.entity.enums.ExpType;
import GaVisionUp.server.entity.enums.PerformanceGrade;
import GaVisionUp.server.entity.exp.Experience;
import GaVisionUp.server.repository.exp.experience.ExperienceRepository;
import GaVisionUp.server.repository.performance.PerformanceReviewRepository;
import GaVisionUp.server.repository.user.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class PerformanceReviewServiceImpl implements PerformanceReviewService {

    private final UserRepository userRepository;
    private final ExperienceRepository experienceRepository;
    private final PerformanceReviewRepository performanceReviewRepository;

    public PerformanceReview evaluatePerformance(Long userId, ExpType expType, PerformanceGrade grade) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사원입니다."));

        int year = LocalDate.now().getYear();
        int newExp = grade.getExp();

        // ✅ 기존 인사평가 조회 (같은 연도 & 분기)
        Optional<PerformanceReview> existingReviewOpt = performanceReviewRepository.findByUserIdAndYearAndExpType(userId, year, expType);
        log.info("🔍 [DEBUG] 기존 평가 조회 결과: {}", existingReviewOpt.isPresent() ? "✅ 있음" : "❌ 없음");

        if (existingReviewOpt.isPresent()) {
            // ✅ 기존 평가가 존재하면 업데이트 검증
            PerformanceReview existingReview = existingReviewOpt.get();
            int previousExp = existingReview.getGrantedExp();

            // ✅ 기존 경험치와 동일하면 중복 저장 방지
            if (previousExp == newExp) {
                log.info("✅ [INFO] 기존 경험치({})와 동일하여 평가 업데이트 생략 (유저 ID: {}, 연도: {}, 분기: {})", newExp, userId, year, expType);
                return existingReview;
            }
            int diffExp = previousExp - newExp;
            user.addExperience(diffExp);

            // ✅ 기존 리뷰 업데이트
            existingReview.updateReview(grade, newExp, expType);
            performanceReviewRepository.save(existingReview);

            // ✅ 기존 경험치 ID 조회 후 업데이트
            Optional<Long> expIdOpt = experienceRepository.findExperienceIdByUserAndYear(userId, expType, year);
            if (expIdOpt.isPresent()) {
                experienceRepository.updateExperienceById(expIdOpt.get(), newExp);
            } else {
                // ✅ 기존 경험치가 없으면 새로 저장
                Experience newExperience = new Experience(user, expType, newExp);
                experienceRepository.save(newExperience);
            }

            return existingReview;
        }

        // ✅ 새로운 인사평가 저장
        PerformanceReview review = PerformanceReview.create(user, expType, grade);
        performanceReviewRepository.save(review);

        // ✅ 새로운 경험치 저장
        Experience experience = new Experience(user, expType, newExp);
        experienceRepository.save(experience);

        return review;
    }


    // ✅ 특정 유저의 인사평가 조회
    public List<PerformanceReview> getPerformanceReviewsByUser(Long userId) {
        return performanceReviewRepository.findByUserId(userId);
    }

    // ✅ 전체 유저의 상반기 인사평가 조회
    public List<PerformanceReview> getAllH1PerformanceReviews() {
        return performanceReviewRepository.findAllByH1Performance();
    }

    // ✅ 전체 유저의 하반기 인사평가 조회
    public List<PerformanceReview> getAllH2PerformanceReviews() {
        return performanceReviewRepository.findAllByH2Performance();
    }
}
