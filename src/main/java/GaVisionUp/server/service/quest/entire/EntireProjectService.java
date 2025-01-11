package GaVisionUp.server.service.quest.entire;

import GaVisionUp.server.entity.quest.EntireProject;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface EntireProjectService {
    EntireProject createEntireProject(Long userId, String projectName, int grantedExp, String note, LocalDate assignedDate);
    Optional<EntireProject> getEntireProjectById(Long id);
    List<EntireProject> getAllEntireProjects();
    List<EntireProject> getEntireProjectsByUser(Long userId);
    List<EntireProject> getEntireProjectsByMonth(int year, int month);
    List<EntireProject> getEntireProjectsByDate(LocalDate date);
}
