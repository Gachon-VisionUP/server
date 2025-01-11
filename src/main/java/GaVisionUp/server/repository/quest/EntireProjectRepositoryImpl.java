package GaVisionUp.server.repository.quest;

import GaVisionUp.server.entity.quest.EntireProject;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static GaVisionUp.server.entity.quest.QEntireProject.entireProject;

@Repository
public class EntireProjectRepositoryImpl implements EntireProjectRepository {

    private final EntityManager em;
    private final JPAQueryFactory queryFactory;

    public EntireProjectRepositoryImpl(EntityManager em) {
        this.em = em;
        this.queryFactory = new JPAQueryFactory(em);
    }

    @Override
    public EntireProject save(EntireProject entireProject) {
        if (entireProject.getId() == null) {
            em.persist(entireProject);
        } else {
            em.merge(entireProject);
        }
        return entireProject;
    }

    @Override
    public Optional<EntireProject> findById(Long id) {
        return Optional.ofNullable(
                queryFactory.selectFrom(entireProject)
                        .where(entireProject.id.eq(id))
                        .fetchOne()
        );
    }

    @Override
    public List<EntireProject> findByUserId(Long userId) {
        return queryFactory
                .selectFrom(entireProject)
                .where(entireProject.user.id.eq(userId))
                .fetch();
    }

    @Override
    public List<EntireProject> findByMonth(int year, int month) {
        return queryFactory
                .selectFrom(entireProject)
                .where(
                        entireProject.assignedDate.year().eq(year),
                        entireProject.assignedDate.month().eq(month)
                )
                .fetch();
    }

    @Override
    public List<EntireProject> findByDate(LocalDate date) {
        return queryFactory
                .selectFrom(entireProject)
                .where(entireProject.assignedDate.eq(date))
                .fetch();
    }

    @Override
    public List<EntireProject> findAll() {
        return queryFactory
                .selectFrom(entireProject)
                .fetch();
    }
}
