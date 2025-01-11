package GaVisionUp.server.repository.quest.job.detail;

import GaVisionUp.server.entity.enums.Cycle;
import GaVisionUp.server.entity.enums.Department;
import GaVisionUp.server.entity.quest.job.JobQuestDetail;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Repository;

import java.util.List;

import static GaVisionUp.server.entity.quest.job.QJobQuestDetail.jobQuestDetail;

@Repository
public class JobQuestDetailRepositoryImpl implements JobQuestDetailRepository {

    private final EntityManager em;
    private final JPAQueryFactory queryFactory;

    public JobQuestDetailRepositoryImpl(EntityManager em) {
        this.em = em;
        this.queryFactory = new JPAQueryFactory(em);
    }

    // ✅ 특정 부서, 직무 그룹, 주기, 월, 주차의 데이터를 조회 (기존 round → month & week 변경)
    public List<JobQuestDetail> findAllByDepartmentAndMonthAndWeek(Department department, int part, Cycle cycle, int month, Integer week) {
        return queryFactory
                .selectFrom(jobQuestDetail)
                .where(
                        jobQuestDetail.department.eq(department),
                        jobQuestDetail.part.eq(part),
                        jobQuestDetail.cycle.eq(cycle),
                        jobQuestDetail.month.eq(month),
                        jobQuestDetail.week.eq(week)
                )
                .fetch(); // ✅ 여러 개의 데이터를 리스트로 반환
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
                .orderBy(jobQuestDetail.month.asc(), jobQuestDetail.week.asc()) // ✅ 월, 주차 기준 정렬
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
