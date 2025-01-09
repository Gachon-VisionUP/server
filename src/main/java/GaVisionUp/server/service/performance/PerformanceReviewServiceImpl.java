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
import org.springframework.stereotype.Service;

import java.util.List;

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

        // ✅ 인사평가 정보 저장
        PerformanceReview review = PerformanceReview.create(user, expType, grade);
        performanceReviewRepository.save(review);

        // ✅ 경험치 추가 로직 실행
        Experience experience = new Experience(user, expType, grade.getExp());
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
