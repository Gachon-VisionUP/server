package GaVisionUp.server.repository.exp.experience;

import GaVisionUp.server.entity.enums.ExpType;
import GaVisionUp.server.entity.exp.ExpBar;
import GaVisionUp.server.entity.exp.Experience;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ExperienceRepository {
    Experience save(Experience experience);  // 개인 경험치 저장
    Optional<Experience> findById(Long id);  // ID로 조회
    List<Experience> findByUserId(Long userId);  // 특정 사용자 경험치 조회
    List<Experience> findByUserIdAndCurrentYear(Long userId, int currentYear, ExpBar expBar);
    List<Experience> findByUserIdAndPreviousYears(Long userId, int previousYear, LocalDate joinDate, ExpBar expBar);
    Optional<Experience> findTopByUserIdOrderByObtainedDateDesc(Long userId);
    List<Experience> findTop3ByUserIdAndYearOrderByObtainedDateDesc(Long userId, int year);
    int getTotalExperienceByYear(Long userId, int year);
    Optional<Long> findExperienceIdByUserAndYear(Long userId, ExpType expType, int year);
    void updateExperienceById(Long expId, int newExp);
    Experience edit(Experience experience);  // 개인 경험치 저장

}