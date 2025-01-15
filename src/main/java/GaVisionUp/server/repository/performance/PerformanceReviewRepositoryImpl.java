package GaVisionUp.server.repository.performance;

import GaVisionUp.server.entity.PerformanceReview;
import GaVisionUp.server.entity.enums.ExpType;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static GaVisionUp.server.entity.QPerformanceReview.performanceReview;

@Repository
public class PerformanceReviewRepositoryImpl implements PerformanceReviewRepository {

    private final EntityManager em;
    private final JPAQueryFactory queryFactory;

    public PerformanceReviewRepositoryImpl(EntityManager em) {
        this.em = em;
        this.queryFactory = new JPAQueryFactory(em);
    }

    // ✅ 인사평가 저장
    @Override
    public PerformanceReview save(PerformanceReview performanceReview) {
        if (performanceReview.getId() == null) {
            em.persist(performanceReview);
        } else {
            em.merge(performanceReview);
        }
        return performanceReview;
    }

    // ✅ 특정 ID로 인사평가 조회
    @Override
    public Optional<PerformanceReview> findById(Long id) {
        return Optional.ofNullable(
                queryFactory
                        .selectFrom(performanceReview)
                        .where(performanceReview.id.eq(id))
                        .fetchOne()
        );
    }

    // ✅ 특정 사용자의 모든 인사평가 조회
    @Override
    public List<PerformanceReview> findByUserId(Long userId) {
        return queryFactory
                .selectFrom(performanceReview)
                .where(performanceReview.user.id.eq(userId))
                .orderBy(performanceReview.evaluationDate.desc()) // 최신순 정렬
                .fetch();
    }

    // ✅ 특정 사용자의 특정 유형(H1Performance, H2Performance)의 인사평가 조회
    @Override
    public List<PerformanceReview> findByUserIdAndExpType(Long userId, ExpType expType) {
        return queryFactory
                .selectFrom(performanceReview)
                .where(
                        performanceReview.user.id.eq(userId),
                        performanceReview.expType.eq(expType)
                )
                .orderBy(performanceReview.evaluationDate.desc()) // 최신순 정렬
                .fetch();
    }

    // ✅ 특정 사용자의 최신 인사평가 조회
    @Override
    public Optional<PerformanceReview> findLatestPerformanceReview(Long userId, ExpType expType) {
        return Optional.ofNullable(
                queryFactory
                        .selectFrom(performanceReview)
                        .where(
                                performanceReview.user.id.eq(userId),
                                performanceReview.expType.eq(expType)
                        )
                        .orderBy(performanceReview.evaluationDate.desc()) // 최신순 정렬
                        .fetchFirst()
        );
    }

    // ✅ 전체 유저의 상반기(H1_PERFORMANCE) 인사평가 조회
    @Override
    public List<PerformanceReview> findAllByH1Performance() {
        return queryFactory
                .selectFrom(performanceReview)
                .where(performanceReview.expType.eq(ExpType.H1_PERFORMANCE))
                .orderBy(performanceReview.evaluationDate.desc()) // 최신순 정렬
                .fetch();
    }

    // ✅ 전체 유저의 하반기(H2_PERFORMANCE) 인사평가 조회
    @Override
    public List<PerformanceReview> findAllByH2Performance() {
        return queryFactory
                .selectFrom(performanceReview)
                .where(performanceReview.expType.eq(ExpType.H2_PERFORMANCE))
                .orderBy(performanceReview.evaluationDate.desc()) // 최신순 정렬
                .fetch();
    }

    @Override
    public Optional<PerformanceReview> findByUserIdAndYearAndExpType(Long userId, int year, ExpType expType) {
        LocalDate startDate = LocalDate.of(year, 1, 1);
        LocalDate endDate = LocalDate.of(year, 12, 31);

        return Optional.ofNullable(
                queryFactory
                        .selectFrom(performanceReview)
                        .where(
                                performanceReview.user.id.eq(userId),
                                performanceReview.evaluationDate.between(startDate, endDate), // ✅ 연도별 검색을 정확하게 함
                                performanceReview.expType.eq(expType)
                        )
                        .fetchOne()
        );
    }

}
