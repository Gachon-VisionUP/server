package GaVisionUp.server.repository.quest.leader;

import GaVisionUp.server.entity.enums.Cycle;
import GaVisionUp.server.entity.enums.Department;
import GaVisionUp.server.entity.quest.leader.LeaderQuest;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

import static GaVisionUp.server.entity.quest.leader.QLeaderQuest.leaderQuest;

@Slf4j
@Repository
public class LeaderQuestRepositoryImpl implements LeaderQuestRepository {

    private final EntityManager em;
    private final JPAQueryFactory queryFactory;

    public LeaderQuestRepositoryImpl(EntityManager em) {
        this.em = em;
        this.queryFactory = new JPAQueryFactory(em);
    }

    // ✅ 특정 유저의 리더 퀘스트 목록 조회
    @Override
    public List<LeaderQuest> findByUserId(Long userId) {
        return queryFactory
                .selectFrom(leaderQuest)
                .where(leaderQuest.user.id.eq(userId))
                .orderBy(leaderQuest.month.asc(), leaderQuest.week.asc().nullsLast()) // ✅ 월, 주차 기준 정렬
                .fetch();
    }

    // ✅ 특정 부서, 주기별 전체 리더 퀘스트 조회
    @Override
    public List<LeaderQuest> findAllByDepartmentAndCycle(Department department, Cycle cycle) {
        return queryFactory
                .selectFrom(leaderQuest)
                .where(
                        leaderQuest.department.eq(department),
                        leaderQuest.cycle.eq(cycle)
                )
                .orderBy(leaderQuest.month.asc(), leaderQuest.week.asc().nullsLast()) // ✅ 월, 주차 기준 정렬
                .fetch();
    }

    // ✅ 특정 부서, 주기 및 round 기준 조회 (WEEKLY)
    @Override
    public Optional<LeaderQuest> findByDepartmentAndCycleAndRound(String department, Cycle cycle, int round) {
        Optional<LeaderQuest> result = Optional.ofNullable(
                queryFactory
                        .selectFrom(leaderQuest)
                        .where(
                                leaderQuest.department.eq(Department.valueOf(department)),
                                leaderQuest.cycle.eq(cycle),
                                leaderQuest.week.eq(round) // ✅ WEEKLY의 경우 주차 기반 조회
                        )
                        .fetchOne()
        );

        log.info("📌 [DEBUG] LeaderQuest (WEEKLY) 조회 결과: {}주차 - {}", round, result.isPresent() ? "존재함" : "없음");
        return result;
    }

    // ✅ 특정 부서, 주기 및 month 기준 조회 (MONTHLY)
    @Override
    public Optional<LeaderQuest> findByDepartmentAndCycleAndMonth(String department, Cycle cycle, int month) {
        Optional<LeaderQuest> result = Optional.ofNullable(
                queryFactory
                        .selectFrom(leaderQuest)
                        .where(
                                leaderQuest.department.eq(Department.valueOf(department)),
                                leaderQuest.cycle.eq(cycle),
                                leaderQuest.month.eq(month) // ✅ MONTHLY의 경우 month 기반 조회
                        )
                        .fetchOne()
        );

        log.info("📌 [DEBUG] LeaderQuest (MONTHLY) 조회 결과: {}월 - {}", month, result.isPresent() ? "존재함" : "없음");
        return result;
    }

    // ✅ 퀘스트 할당 저장
    @Override
    public LeaderQuest save(LeaderQuest leaderQuest) {
        if (leaderQuest.getId() == null) {
            em.persist(leaderQuest);
        } else {
            em.merge(leaderQuest);
        }
        return leaderQuest;
    }
}
