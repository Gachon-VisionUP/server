package GaVisionUp.server.repository.jobquest;

import GaVisionUp.server.entity.JobQuest;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

import static GaVisionUp.server.entity.QJobQuest.jobQuest;

@Repository
public class JobQuestRepositoryImpl implements JobQuestRepository {

    private final EntityManager em;
    private final JPAQueryFactory queryFactory;

    public JobQuestRepositoryImpl(EntityManager em) {
        this.em = em;
        this.queryFactory = new JPAQueryFactory(em);
    }

    // ✅ 특정 부서, 직무 그룹, 주기 및 회차로 조회
    @Override
    public Optional<JobQuest> findByDepartmentAndRound(String department, int part, String cycle, int round) {
        return Optional.ofNullable(
                queryFactory
                        .selectFrom(jobQuest)
                        .where(
                                jobQuest.department.stringValue().eq(department),
                                jobQuest.part.eq(part),
                                jobQuest.cycle.stringValue().eq(cycle),
                                jobQuest.round.eq(round)
                        )
                        .fetchOne()
        );
    }

    // ✅ 특정 부서, 직무 그룹의 전체 기록 조회 (주기별 정렬)
    @Override
    public List<JobQuest> findAllByDepartment(String department, int part, String cycle) {
        return queryFactory
                .selectFrom(jobQuest)
                .where(
                        jobQuest.department.stringValue().eq(department),
                        jobQuest.part.eq(part),
                        jobQuest.cycle.stringValue().eq(cycle)
                )
                .orderBy(jobQuest.round.asc()) // ✅ 회차 기준 정렬
                .fetch();
    }

    // ✅ 경험치 부여 기록 저장
    @Override
    public JobQuest save(JobQuest jobQuest) {
        if (jobQuest.getId() == null) {
            em.persist(jobQuest);
        } else {
            em.merge(jobQuest);
        }
        return jobQuest;
    }
}
