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

    // ✅ 특정 부서, 직무 그룹, 주기 및 회차로 조회
    @Override
    public Optional<JobQuest> findByDepartmentAndRound(String department, int part, String cycle, int round) {
        Optional<JobQuest> result = Optional.ofNullable(
                queryFactory
                        .selectFrom(jobQuest)
                        .where(
                                jobQuest.department.eq(Department.valueOf(department)),
                                jobQuest.part.eq(part),
                                jobQuest.cycle.eq(Cycle.valueOf(cycle)),
                                jobQuest.round.eq(round) // ✅ 정확한 round 기준 조회
                        )
                        .fetchOne()
        );

        log.info("📌 [DEBUG] JobQuest 조회 결과: {}", result.isPresent() ? "존재함" : "없음");
        return result;
    }

    // ✅ 특정 부서, 직무 그룹, 월, 주차 기준으로 조회
    @Override
    public Optional<JobQuest> findByDepartmentAndMonthAndWeek(String department, int part, int month, Integer week) {
        Optional<JobQuest> result = Optional.ofNullable(
                queryFactory
                        .selectFrom(jobQuest)
                        .where(
                                jobQuest.department.eq(Department.valueOf(department)),
                                jobQuest.part.eq(part),
                                jobQuest.month.eq(month),
                                jobQuest.week.eq(week)
                        )
                        .fetchOne()
        );

        if (result.isPresent()) {
            log.info("✅ [INFO] JobQuest 조회 성공: {} {}월 {}주차 - {}", department, month, week, result.get().getQuestGrade());
        } else {
            log.warn("⚠️ [WARN] JobQuest 조회 실패: {} {}월 {}주차 데이터 없음", department, month, week);
        }

        return result;
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
