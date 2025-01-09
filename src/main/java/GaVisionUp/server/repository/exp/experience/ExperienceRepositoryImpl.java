package GaVisionUp.server.repository.exp.experience;

import GaVisionUp.server.entity.User;
import GaVisionUp.server.entity.exp.ExpBar;
import GaVisionUp.server.entity.exp.Experience;
import GaVisionUp.server.entity.exp.QExperience;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
@Transactional
public class ExperienceRepositoryImpl implements ExperienceRepository {

    private final EntityManager em;
    private final JPAQueryFactory queryFactory;
    private final QExperience qExperience = QExperience.experience;

    public ExperienceRepositoryImpl(EntityManager em) {
        this.em = em;
        this.queryFactory = new JPAQueryFactory(em);
    }

    @Override
    public Experience save(Experience experience) {
        // ✅ User의 totalExp 업데이트 (Experience 생성 시 User.addExperience() 호출됨)
        User user = experience.getUser();
        if (user == null) {
            throw new IllegalArgumentException("Experience 저장 시 User 정보가 필요합니다.");
        }

        user.addExperience(experience.getExp()); // ✅ 경험치 추가
        em.merge(user); // ✅ User의 totalExp 변경 반영

        if (experience.getId() == null) {
            em.persist(experience);
        } else {
            em.merge(experience);
        }

        return experience;
    }

    @Override
    public Optional<Experience> findById(Long id) {
        return Optional.ofNullable(
                queryFactory
                        .selectFrom(qExperience)
                        .where(qExperience.id.eq(id))
                        .fetchOne()
        );
    }

    @Override
    public List<Experience> findByUserId(Long userId) {
        return queryFactory
                .selectFrom(qExperience)
                .where(qExperience.user.id.eq(userId))
                .fetch();
    }

    // ✅ 올해(Current Year) 경험치 조회 및 총 경험치 반영
    @Override
    public List<Experience> findByUserIdAndCurrentYear(Long userId, int currentYear, ExpBar expBar) {
        List<Experience> experiences = queryFactory
                .selectFrom(qExperience)
                .where(
                        qExperience.user.id.eq(userId)
                                .and(qExperience.obtainedDate.year().eq(currentYear)) // ✅ 현재 연도 필터링
                )
                .fetch();

        // ✅ currentTotalExp 업데이트
        int currentTotalExp = experiences.stream().mapToInt(Experience::getExp).sum();
        expBar.setCurrentTotalExp(currentTotalExp);
        em.merge(expBar); // 업데이트 적용

        return experiences;
    }

    // ✅ 작년(Previous Years)까지 경험치 조회 및 총 경험치 반영
    @Override
    public List<Experience> findByUserIdAndPreviousYears(Long userId, int previousYear, LocalDate joinDate, ExpBar expBar) {
        List<Experience> experiences = queryFactory
                .selectFrom(qExperience)
                .where(
                        qExperience.user.id.eq(userId)
                                .and(qExperience.obtainedDate.year().loe(previousYear)) // ✅ 작년까지 필터링
                                .and(qExperience.obtainedDate.goe(joinDate)) // ✅ 유저 입사일부터 필터링
                )
                .fetch();

        // ✅ previousTotalExp 업데이트
        int previousTotalExp = experiences.stream().mapToInt(Experience::getExp).sum();
        expBar.setPreviousTotalExp(previousTotalExp);
        em.merge(expBar); // 업데이트 적용

        return experiences;
    }
}
