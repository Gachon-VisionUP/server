package GaVisionUp.server.repository.quest.job;

import GaVisionUp.server.entity.enums.Cycle;
import GaVisionUp.server.entity.enums.Department;
import GaVisionUp.server.entity.quest.job.JobQuest;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

import static GaVisionUp.server.entity.quest.job.QJobQuest.jobQuest;

@Slf4j
@Repository
public class JobQuestRepositoryImpl implements JobQuestRepository {

    private final EntityManager em;
    private final JPAQueryFactory queryFactory;

    public JobQuestRepositoryImpl(EntityManager em) {
        this.em = em;
        this.queryFactory = new JPAQueryFactory(em);
    }

    // ✅ 특정 부서, 직무 그룹, 주기 및 round 기준 조회
    @Override
    public Optional<JobQuest> findByDepartmentAndCycleAndRound(String department, int part, Cycle cycle, int round) {
        Optional<JobQuest> result = Optional.ofNullable(
                queryFactory
                        .selectFrom(jobQuest)
                        .where(
                                jobQuest.department.eq(Department.valueOf(department)),
                                jobQuest.part.eq(part),
                                jobQuest.cycle.eq(cycle),
                                jobQuest.round.eq(round) // ✅ round 기준 조회
                        )
                        .fetchOne()
        );

        log.info("📌 [DEBUG] JobQuest 조회 결과 ({} - {}): {}", cycle, round, result.isPresent() ? "존재함" : "없음");
        return result;
    }

    // ✅ 특정 부서, 직무 그룹, 주기 및 month 기준 조회 (MONTHLY 전용)
    @Override
    public Optional<JobQuest> findByDepartmentAndCycleAndMonth(String department, int part, Cycle cycle, int month) {
        Optional<JobQuest> result = Optional.ofNullable(
                queryFactory
                        .selectFrom(jobQuest)
                        .where(
                                jobQuest.department.eq(Department.valueOf(department)),
                                jobQuest.part.eq(part),
                                jobQuest.cycle.eq(cycle),
                                jobQuest.round.eq(month) // ✅ MONTHLY의 경우 round = month
                        )
                        .fetchOne()
        );

        log.info("📌 [DEBUG] JobQuest (MONTHLY) 조회 결과: {}월 - {}", month, result.isPresent() ? "존재함" : "없음");
        return result;
    }

    // ✅ 특정 부서, 직무 그룹의 전체 기록 조회 (주기별 정렬)
    @Override
    public List<JobQuest> findAllByDepartment(String department, int part, Cycle cycle) {
        return queryFactory
                .selectFrom(jobQuest)
                .where(
                        jobQuest.department.eq(Department.valueOf(department)),
                        jobQuest.part.eq(part),
                        jobQuest.cycle.eq(cycle)
                )
                .orderBy(jobQuest.round.asc()) // ✅ round 기준 정렬
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
