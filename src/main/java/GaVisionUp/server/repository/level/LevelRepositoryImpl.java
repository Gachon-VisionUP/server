package GaVisionUp.server.repository.level;

import GaVisionUp.server.entity.Level;
import GaVisionUp.server.entity.enums.JobGroup;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

import static GaVisionUp.server.entity.QLevel.level;

@Repository
public class LevelRepositoryImpl implements LevelRepository {
    private final EntityManager em;
    private final JPAQueryFactory queryFactory;

    public LevelRepositoryImpl(EntityManager em) {
        this.em = em;
        this.queryFactory = new JPAQueryFactory(em);
    }

    // ✅ 특정 직군(JobGroup)의 모든 레벨 조회
    @Override
    public List<Level> findByJobGroup(JobGroup jobGroup) {
        return queryFactory
                .selectFrom(level)
                .where(level.jobGroup.eq(jobGroup))
                .orderBy(level.requiredExp.asc()) // 경험치 순 정렬
                .fetch();
    }

    // ✅ 특정 직군에서 경험치에 해당하는 레벨 찾기 (경험치 이하 중 가장 높은 레벨)
    @Override
    public Optional<Level> findLevelByExp(JobGroup jobGroup, int totalExp) {
        return Optional.ofNullable(
                queryFactory
                        .selectFrom(level)
                        .where(
                                level.jobGroup.eq(jobGroup),
                                level.requiredExp.loe(totalExp) // 경험치 이하 중 가장 높은 레벨
                        )
                        .orderBy(level.requiredExp.desc()) // 내림차순 정렬 (가장 높은 경험치)
                        .fetchFirst()
        );
    }

    // ✅ 현재 레벨보다 높은 경험치가 있을 경우, 새로운 레벨 반환 (레벨업 판정)
    @Override
    public Optional<Level> findNextLevel(JobGroup jobGroup, int totalExp, String currentLevelName) {
        return Optional.ofNullable(
                queryFactory
                        .selectFrom(level)
                        .where(
                                level.jobGroup.eq(jobGroup),
                                level.requiredExp.loe(totalExp), // ✅ 현재 경험치 이하 중 가장 높은 레벨 선택
                                level.requiredExp.gt(this.findLevelByExp(jobGroup, totalExp) // ✅ 여기 수정!
                                        .map(Level::getRequiredExp).orElse(0)) // ✅ 현재 레벨보다 높은 값만 필터링
                        )
                        .orderBy(level.requiredExp.asc()) // ✅ 가장 가까운 상위 레벨 선택
                        .fetchFirst()
        );
    }

}
