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

    // ✅ 현재 레벨 조회 (레벨명 + 직군 기반)
    @Override
    public Optional<Level> findByLevelNameAndJobGroup(String levelName, JobGroup jobGroup) {
        return Optional.ofNullable(
                queryFactory
                        .selectFrom(level)
                        .where(
                                level.levelName.eq(levelName),
                                level.jobGroup.eq(jobGroup)
                        )
                        .fetchOne()
        );
    }

    // ✅ 다음 레벨 조회 (현재 레벨보다 높은 경험치를 필요로 하는 레벨)
    @Override
    public Optional<Level> findNextLevel(JobGroup jobGroup, int totalExp, String currentLevelName) {
        return Optional.ofNullable(
                queryFactory
                        .selectFrom(level)
                        .where(
                                level.jobGroup.eq(jobGroup),
                                level.requiredExp.gt(totalExp), // ✅ 현재 경험치보다 높은 값
                                level.levelName.ne(currentLevelName) // ✅ 현재 레벨 제외
                        )
                        .orderBy(level.requiredExp.asc()) // ✅ 다음 레벨을 찾기 위해 정렬
                        .fetchFirst()
        );
    }

    @Override
    public Optional<Level> findById(Long levelId) {
        return Optional.empty();
    }

    @Override
    public Optional<Level> findByLevelName(String levelName) {
        return Optional.ofNullable(
                queryFactory
                        .selectFrom(level)
                        .where(
                                level.levelName.eq(levelName)
                        )
                        .fetchOne()
        );
    }

    @Override
    public Optional<Level> findByJobGroupAndLevelName(JobGroup jobGroup, String levelName) {

        Level result = queryFactory.selectFrom(level)
                .where(level.jobGroup.eq(jobGroup)
                        .and(level.levelName.eq(levelName)))
                .fetchOne();

        return Optional.ofNullable(result);
    }

    @Override
    public Level save(Level level) {
        if (level.getId() == null) {
            em.persist(level); // 새로운 엔티티 저장
            return level;
        } else {
            return em.merge(level); // 기존 엔티티 업데이트
        }
    }
}
