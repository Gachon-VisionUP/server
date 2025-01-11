package GaVisionUp.server.repository.quest.team;

import GaVisionUp.server.entity.quest.TeamQuest;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Repository;

import java.util.List;

import static GaVisionUp.server.entity.quest.QTeamQuest.teamQuest;

@Repository
public class TeamQuestRepositoryImpl implements TeamQuestRepository {

    private final EntityManager em;
    private final JPAQueryFactory queryFactory;

    public TeamQuestRepositoryImpl(EntityManager em) {
        this.em = em;
        this.queryFactory = new JPAQueryFactory(em);
    }

    // ✅ 특정 유저의 월별 팀 퀘스트 기록 조회
    @Override
    public List<TeamQuest> findByUserAndMonth(Long userId, int year, int month) {
        return queryFactory
                .selectFrom(teamQuest)
                .where(
                        teamQuest.user.id.eq(userId),
                        teamQuest.recordedDate.year().eq(year),
                        teamQuest.recordedDate.month().eq(month)
                )
                .orderBy(teamQuest.recordedDate.asc()) // ✅ 기록일 기준 정렬
                .fetch();
    }

    // ✅ 팀 퀘스트 기록 저장
    @Override
    public TeamQuest save(TeamQuest quest) {
        if (quest.getId() == null) {
            em.persist(quest);
        } else {
            em.merge(quest);
        }
        return quest;
    }
}
