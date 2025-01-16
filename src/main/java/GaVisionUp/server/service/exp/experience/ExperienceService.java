package GaVisionUp.server.service.exp.experience;

import GaVisionUp.server.entity.enums.ExpType;
import GaVisionUp.server.entity.exp.Experience;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface ExperienceService {
    Experience addExperience(Long userId, ExpType expType, int exp);
    Optional<Experience> getExperienceById(Long id);
    List<Experience> getExperiencesByUserId(Long userId);
    List<Experience> getExperiencesByCurrentYear(Long userId, int currentYear);
    List<Experience> getExperiencesByPreviousYears(Long userId, int previousYear);

    Optional<Experience> getLatestExperienceByUserId(Long userId);
    List<Experience> getTop3ExperiencesByYear(Long userId, int year);
    Map<Integer, Integer> getYearlyTotalExperience(Long userId, int startYear, int endYear);
}
