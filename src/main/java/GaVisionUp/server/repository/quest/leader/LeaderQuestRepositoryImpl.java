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

    @Override
    public Optional<LeaderQuest> findById(Long id) {
        return Optional.ofNullable(
                queryFactory
                        .selectFrom(leaderQuest)
                        .where(leaderQuest.id.eq(id))
                        .fetchOne()
        );
    }

    // ✅ 특정 유저 ID 및 연도별 퀘스트 조회 (MONTHLY + WEEKLY 포함)
    @Override
    public List<LeaderQuest> findByUserIdAndYear(Long userId, int year) {
        return queryFactory
                .selectFrom(leaderQuest)
                .where(
                        leaderQuest.user.id.eq(userId),
                        leaderQuest.assignedDate.year().eq(year)
                )
                .orderBy(leaderQuest.month.asc(), leaderQuest.week.asc().nullsLast()) // ✅ 월, 주차 정렬
                .fetch();
    }

    // ✅ 특정 유저의 특정 퀘스트 수행 기록 조회 (User ID + Quest ID)
    @Override
    public Optional<LeaderQuest> findByUserIdAndQuestId(Long userId, Long questId) {
        return Optional.ofNullable(
                queryFactory
                        .selectFrom(leaderQuest)
                        .where(
                                leaderQuest.user.id.eq(userId),
                                leaderQuest.id.eq(questId)
                        )
                        .fetchOne()
        );
    }

    // ✅ 특정 유저가 특정 퀘스트 조건 ID에 해당하는 모든 리더 퀘스트 조회
    @Override
    public List<LeaderQuest> findByUserIdAndConditionId(Long userId, Long conditionId) {
        return queryFactory
                .selectFrom(leaderQuest)
                .where(
                        leaderQuest.user.id.eq(userId),
                        leaderQuest.condition.id.eq(conditionId) // ✅ 동일한 퀘스트 조건 ID 필터링
                )
                .orderBy(leaderQuest.assignedDate.asc()) // ✅ 달성 날짜 기준 정렬
                .fetch();
    }
}
