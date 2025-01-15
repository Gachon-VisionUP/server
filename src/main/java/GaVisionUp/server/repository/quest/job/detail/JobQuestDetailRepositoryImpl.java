package GaVisionUp.server.repository.quest.job.detail;

import GaVisionUp.server.entity.enums.Cycle;
import GaVisionUp.server.entity.enums.Department;
import GaVisionUp.server.entity.quest.job.JobQuestDetail;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
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

    // ✅ 특정 부서, 직무 그룹, 주기, round 기반으로 조회 (새롭게 추가됨)
    @Override
    public List<JobQuestDetail> findAllByDepartmentAndCycleAndRound(Department department, int part, Cycle cycle, int round) {
        return queryFactory
                .selectFrom(jobQuestDetail)
                .where(
                        jobQuestDetail.department.eq(department),
                        jobQuestDetail.part.eq(part),
                        jobQuestDetail.cycle.eq(cycle),
                        jobQuestDetail.round.eq(round) // ✅ round 값 기준 조회
                )
                .orderBy(jobQuestDetail.round.asc()) // ✅ round 기준 정렬 추가
                .fetch();
    }

    // ✅ 특정 부서, 직무 그룹, 주기의 모든 데이터를 조회
    @Override
    public List<JobQuestDetail> findAllByDepartmentAndCycle(Department department, int part, Cycle cycle) {
        return queryFactory
                .selectFrom(jobQuestDetail)
                .where(
                        jobQuestDetail.department.eq(department),
                        jobQuestDetail.part.eq(part),
                        jobQuestDetail.cycle.eq(cycle)
                )
                .orderBy(jobQuestDetail.month.asc(), jobQuestDetail.round.asc()) // ✅ month 및 round 기준 정렬
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

    @Override
    public Optional<JobQuestDetail> findByRecordedDate(LocalDate recordedDate) {
        return Optional.ofNullable(
                queryFactory
                        .selectFrom(jobQuestDetail)
                        .where(jobQuestDetail.recordedDate.eq(recordedDate))
                        .fetchOne()
        );
    }

    @Override
    public List<JobQuestDetail> findAllJobQuests() {
        return queryFactory
                .selectFrom(jobQuestDetail)
                .orderBy(jobQuestDetail.recordedDate.asc()) // 날짜 오름차순 정렬
                .fetch();
    }
}
