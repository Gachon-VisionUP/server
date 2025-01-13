package GaVisionUp.server.repository.quest.leader.condition;

import GaVisionUp.server.entity.enums.Cycle;
import GaVisionUp.server.entity.enums.Department;
import GaVisionUp.server.entity.quest.leader.LeaderQuestCondition;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

import static GaVisionUp.server.entity.quest.leader.QLeaderQuestCondition.leaderQuestCondition;

@Repository
public class LeaderQuestConditionRepositoryImpl implements LeaderQuestConditionRepository {

    private final EntityManager em;
    private final JPAQueryFactory queryFactory;

    public LeaderQuestConditionRepositoryImpl(EntityManager em) {
        this.em = em;
        this.queryFactory = new JPAQueryFactory(em);
    }

    // ✅ 특정 부서 및 주기의 퀘스트 조건 조회
    @Override
    public List<LeaderQuestCondition> findAllByDepartmentAndCycle(Department department, Cycle cycle) {
        return queryFactory
                .selectFrom(leaderQuestCondition)
                .where(
                        leaderQuestCondition.department.eq(department),
                        leaderQuestCondition.cycle.eq(cycle)
                )
                .fetch();
    }

    // ✅ 특정 퀘스트명으로 조회
    @Override
    public Optional<LeaderQuestCondition> findByQuestName(String questName) {
        return Optional.ofNullable(
                queryFactory
                        .selectFrom(leaderQuestCondition)
                        .where(leaderQuestCondition.questName.eq(questName))
                        .fetchOne()
        );
    }

    // ✅ 퀘스트 조건 저장
    @Override
    public LeaderQuestCondition save(LeaderQuestCondition questCondition) {
        if (questCondition.getId() == null) {
            em.persist(questCondition);
        } else {
            em.merge(questCondition);
        }
        return questCondition;
    }

    // ✅ 특정 부서의 퀘스트 조건 조회
    @Override
    public List<LeaderQuestCondition> findByDepartment(Department department) {
        return queryFactory
                .selectFrom(leaderQuestCondition)
                .where(leaderQuestCondition.department.eq(department))
                .fetch();
    }
}