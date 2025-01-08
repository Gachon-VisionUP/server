package GaVisionUp.server.repository.exp.experience;

import GaVisionUp.server.entity.User;
import GaVisionUp.server.entity.exp.Experience;
import GaVisionUp.server.entity.exp.QExperience;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

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
}
