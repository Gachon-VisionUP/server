package GaVisionUp.server.repository.jobquest.job.detail;

import GaVisionUp.server.entity.enums.Cycle;
import GaVisionUp.server.entity.quest.job.JobQuestDetail;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import static GaVisionUp.server.entity.quest.job.QJobQuestDetail.jobQuestDetail;

@Repository
public class JobQuestDetailRepositoryImpl implements JobQuestDetailRepository {

    private final EntityManager em;
    private final JPAQueryFactory queryFactory;

    public JobQuestDetailRepositoryImpl(EntityManager em) {
        this.em = em;
        this.queryFactory = new JPAQueryFactory(em);
    }

    // ✅ 특정 부서, 직무 그룹, 주기, 회차의 데이터를 조회
    @Override
    public Optional<JobQuestDetail> findByDepartmentAndRound(String department, int part, Cycle cycle, int round) {
        return Optional.ofNullable(
                queryFactory
                        .selectFrom(jobQuestDetail)
                        .where(
                                jobQuestDetail.department.stringValue().eq(department),
                                jobQuestDetail.part.eq(part),
                                jobQuestDetail.cycle.eq(cycle),
                                jobQuestDetail.round.eq(round)
                        )
                        .fetchOne()
        );
    }

    // ✅ 특정 부서, 직무 그룹, 주기의 모든 데이터를 조회
    @Override
    public List<JobQuestDetail> findAllByDepartmentAndCycle(String department, int part, Cycle cycle) {
        return queryFactory
                .selectFrom(jobQuestDetail)
                .where(
                        jobQuestDetail.department.stringValue().eq(department),
                        jobQuestDetail.part.eq(part),
                        jobQuestDetail.cycle.eq(cycle)
                )
                .orderBy(jobQuestDetail.round.asc()) // ✅ 회차 기준 오름차순 정렬
                .fetch();
    }

    // ✅ JobQuestDetail 저장
    @Override
    public JobQuestDetail save(JobQuestDetail jobQuestDetail) {
        if (jobQuestDetail.getId() == null) {
            em.persist(jobQuestDetail);
        } else {
            em.merge(jobQuestDetail);
        }
        return jobQuestDetail;
    }
}