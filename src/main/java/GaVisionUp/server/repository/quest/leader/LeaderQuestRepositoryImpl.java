package GaVisionUp.server.repository.quest.leader;

import GaVisionUp.server.entity.enums.Cycle;
import GaVisionUp.server.entity.enums.Department;
import GaVisionUp.server.entity.quest.leader.LeaderQuest;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Repository;

import java.util.List;

import static GaVisionUp.server.entity.quest.leader.QLeaderQuest.leaderQuest;

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

    // ✅ 특정 부서, 주기별 전체 리더 퀘스트 조회 (소속별 분리)
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
}
