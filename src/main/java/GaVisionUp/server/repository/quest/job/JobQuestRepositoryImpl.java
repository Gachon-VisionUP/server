package GaVisionUp.server.repository.quest.job;

import GaVisionUp.server.entity.User;
import GaVisionUp.server.entity.enums.Cycle;
import GaVisionUp.server.entity.enums.Department;
import GaVisionUp.server.entity.quest.job.JobQuest;
import GaVisionUp.server.repository.user.UserRepository;
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
    private final UserRepository userRepository;

    public JobQuestRepositoryImpl(EntityManager em, UserRepository userRepository) {
        this.em = em;
        this.queryFactory = new JPAQueryFactory(em);
        this.userRepository = userRepository;
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

    // ✅ 소속 & 직무그룹 & 연도별 직무별 퀘스트 조회
    @Override
    public List<JobQuest> findByDepartmentAndPartAndYear(String department, int part, int year) {
        return queryFactory
                .selectFrom(jobQuest)
                .where(
                        jobQuest.department.eq(Department.valueOf(department)),
                        jobQuest.part.eq(part),
                        jobQuest.grantedDate.year().eq(year)
                )
                .orderBy(jobQuest.round.asc()) // ✅ round 순서대로 정렬
                .fetch();
    }

    // ✅ 특정 유저의 소속 & 직무 그룹을 기반으로 연도별 JobQuest 조회
    @Override
    public List<JobQuest> findByDepartmentAndPartAndYear(Long userId, int year) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        return queryFactory
                .selectFrom(jobQuest)
                .where(
                        jobQuest.department.eq(user.getDepartment()), // ✅ 소속 기준 필터링
                        jobQuest.part.eq(user.getPart()), // ✅ 직무 그룹 기준 필터링
                        jobQuest.grantedDate.year().eq(year) // ✅ 해당 연도 기준 필터링
                )
                .orderBy(jobQuest.round.asc()) // ✅ round 순서대로 정렬
                .fetch();
    }
}
