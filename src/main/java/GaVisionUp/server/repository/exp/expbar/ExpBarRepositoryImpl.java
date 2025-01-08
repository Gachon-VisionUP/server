package GaVisionUp.server.repository.exp.expbar;

import GaVisionUp.server.entity.exp.ExpBar;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;

import jakarta.transaction.Transactional;
import org.springframework.stereotype.Repository;

import java.util.Optional;

import static GaVisionUp.server.entity.exp.QExpBar.expBar;

@Repository
@Transactional
public class ExpBarRepositoryImpl implements ExpBarRepository {
    private final EntityManager em;
    private final JPAQueryFactory queryFactory;

    public ExpBarRepositoryImpl(EntityManager em) {
        this.em = em;
        this.queryFactory = new JPAQueryFactory(em);
    }


    @Override
    public ExpBar save(ExpBar expBar) {
        if (expBar.getId() == null) {
            em.persist(expBar); // 새 객체 저장
        } else {
            em.merge(expBar); // 기존 객체 업데이트
        }
        return expBar;
    }

    @Override
    public Optional<ExpBar> findById(Long id) {
        return Optional.ofNullable(em.find(ExpBar.class, id));
    }

    @Override
    public Optional<ExpBar> findByUserId(Long userId) {
        return Optional.ofNullable(
                queryFactory
                        .selectFrom(expBar)
                        .where(expBar.userId.eq(userId))
                        .fetchOne()
        );
    }

    @Override
    public void updateTotalExp(Long userId, int exp) {
        ExpBar foundExpBar = queryFactory
                .selectFrom(expBar)
                .where(expBar.userId.eq(userId))
                .fetchOne();

        if (foundExpBar != null) {
            foundExpBar.setTotalExp(foundExpBar.getTotalExp() + exp);
            em.merge(foundExpBar);
        }
    }
}