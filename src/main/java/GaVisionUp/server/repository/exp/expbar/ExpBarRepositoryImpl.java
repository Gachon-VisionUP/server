package GaVisionUp.server.repository.exp.expbar;

import GaVisionUp.server.entity.exp.ExpBar;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Repository;

import java.util.Optional;

import static GaVisionUp.server.entity.exp.QExpBar.expBar;

@Repository
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
            em.persist(expBar); // 신규 저장
        } else {
            em.merge(expBar); // 기존 데이터 업데이트
        }
        return expBar;
    }

    @Override
    public Optional<ExpBar> findById(Long id) {
        return Optional.ofNullable(
                queryFactory
                        .selectFrom(expBar)
                        .where(expBar.id.eq(id))
                        .fetchOne()
        );
    }

    @Override
    public Optional<ExpBar> findByUserId(Long userId) {
        return Optional.ofNullable(
                queryFactory
                        .selectFrom(expBar)
                        .where(expBar.user.id.eq(userId))
                        .fetchOne()
        );
    }
}
