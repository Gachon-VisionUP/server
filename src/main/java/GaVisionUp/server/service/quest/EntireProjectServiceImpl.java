package GaVisionUp.server.service.quest;

import GaVisionUp.server.entity.User;
import GaVisionUp.server.entity.enums.ExpType;
import GaVisionUp.server.entity.exp.Experience;
import GaVisionUp.server.entity.quest.EntireProject;
import GaVisionUp.server.repository.exp.experience.ExperienceRepository;
import GaVisionUp.server.repository.quest.EntireProjectRepository;
import GaVisionUp.server.repository.user.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class EntireProjectServiceImpl implements EntireProjectService {

    private final EntireProjectRepository entireProjectRepository;
    private final UserRepository userRepository;
    private final ExperienceRepository experienceRepository;

    @Override
    public EntireProject createEntireProject(Long userId, String projectName, int grantedExp, String note, LocalDate assignedDate) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("유저를 찾을 수 없습니다."));

        EntireProject project = EntireProject.create(user, projectName, grantedExp, note, assignedDate);
        entireProjectRepository.save(project);

        // ✅ 경험치 기록 저장
        Experience experience = new Experience(user, ExpType.ENTIRE_PROJECT, grantedExp);
        experienceRepository.save(experience);
        return project;
    }

    @Override
    public Optional<EntireProject> getEntireProjectById(Long id) {
        return entireProjectRepository.findById(id);
    }

    @Override
    public List<EntireProject> getAllEntireProjects() {
        return entireProjectRepository.findAll();
    }

    @Override
    public List<EntireProject> getEntireProjectsByUser(Long userId) {
        return entireProjectRepository.findByUserId(userId);
    }

    @Override
    public List<EntireProject> getEntireProjectsByMonth(int year, int month) {
        return entireProjectRepository.findByMonth(year, month);
    }

    @Override
    public List<EntireProject> getEntireProjectsByDate(LocalDate date) {
        return entireProjectRepository.findByDate(date);
    }
}
