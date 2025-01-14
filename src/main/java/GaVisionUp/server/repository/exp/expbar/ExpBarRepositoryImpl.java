package GaVisionUp.server.repository.exp.expbar;

import GaVisionUp.server.entity.User;
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
        // ✅ 유저 ID 중복 검사 (ExpBar는 유저당 하나만 존재)
        Optional<ExpBar> existingExpBar = findByUserId(expBar.getUser().getId());

        if (existingExpBar.isPresent()) {
            throw new IllegalArgumentException("해당 유저의 경험치 바가 이미 존재합니다.");
        }

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

    // ✅ 특정 유저의 ExpBar 생성 (ExpBarService에서 사용)
    @Override
    public ExpBar createExpBarForUser(User user) {
        ExpBar newExpBar = new ExpBar(user);
        em.persist(newExpBar);
        return newExpBar;
    }
}
