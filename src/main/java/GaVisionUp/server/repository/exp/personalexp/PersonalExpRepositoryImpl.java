package GaVisionUp.server.repository.exp.personalexp;

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

    public PersonalExpRepositoryImpl(EntityManager em) {
        this.em = em;
        this.queryFactory = new JPAQueryFactory(em);
    }


    @Override
    public PersonalExp save(PersonalExp personalExp) {
        if (personalExp.getId() == null) {
            em.persist(personalExp);
        } else {
            em.merge(personalExp);
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
}