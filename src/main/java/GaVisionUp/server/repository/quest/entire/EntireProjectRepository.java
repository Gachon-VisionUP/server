package GaVisionUp.server.repository.quest.entire;

import GaVisionUp.server.entity.quest.EntireProject;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface EntireProjectRepository {
    EntireProject save(EntireProject entireProject);

    Optional<EntireProject> findById(Long id);

    List<EntireProject> findByUserId(Long userId);

    List<EntireProject> findByMonth(int year, int month);

    List<EntireProject> findByDate(LocalDate date);

    List<EntireProject> findAll();
}
