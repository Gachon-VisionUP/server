package GaVisionUp.server.repository.exp.personalexp;

import GaVisionUp.server.entity.exp.ExpBar;
import GaVisionUp.server.entity.exp.PersonalExp;
import GaVisionUp.server.entity.exp.QExpBar;
import GaVisionUp.server.entity.exp.QPersonalExp;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class PersonalExpRepositoryImpl implements PersonalExpRepository {

    private final EntityManager em;
    private final JPAQueryFactory queryFactory;

    private final QPersonalExp qPersonalExp = QPersonalExp.personalExp;
    private final QExpBar qExpBar = QExpBar.expBar;

    public PersonalExpRepositoryImpl(EntityManager em) {
        this.em = em;
        this.queryFactory = new JPAQueryFactory(em);
    }

    @Override
    public PersonalExp save(PersonalExp personalExp) {
        // ExpBar에서 해당 사용자의 총 경험치를 증가시킴
        ExpBar expBar = queryFactory
                .selectFrom(qExpBar)
                .where(qExpBar.userId.eq(personalExp.getUsers().getId()))
                .fetchOne();

        if (expBar != null) {
            // ExpBar의 totalExp에 추가 경험치를 누적
            expBar.setTotalExp(expBar.getTotalExp() + personalExp.getExp());
            em.merge(expBar); // 변경 내용을 DB에 반영
        }

        // PersonalExp 저장
        if (personalExp.getId() == null) {
            em.persist(personalExp); // 새 객체 저장
        } else {
            em.merge(personalExp); // 기존 객체 업데이트
        }
        return personalExp;
    }

    @Override
    public Optional<PersonalExp> findById(Long id) {
        return Optional.ofNullable(
                queryFactory
                        .selectFrom(qPersonalExp)
                        .where(qPersonalExp.id.eq(id))
                        .fetchOne()
        );
    }

    @Override
    public List<PersonalExp> findByUserId(Long userId) {
        return queryFactory
                .selectFrom(qPersonalExp)
                .where(qPersonalExp.users.id.eq(userId))
                .fetch();
    }

    @Override
    public void addExperience(Long userId, int exp) {
        ExpBar expBar = queryFactory
                .selectFrom(qExpBar)
                .where(qExpBar.userId.eq(userId))
                .fetchOne();

        if (expBar != null) {
            expBar.setTotalExp(expBar.getTotalExp() + exp);
            em.merge(expBar);
        }
    }
}
