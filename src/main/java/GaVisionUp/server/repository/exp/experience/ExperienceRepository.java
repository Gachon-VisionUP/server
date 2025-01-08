package GaVisionUp.server.repository.exp.experience;

import GaVisionUp.server.entity.exp.Experience;

import java.util.List;
import java.util.Optional;

public interface ExperienceRepository {
    Experience save(Experience experience);  // 개인 경험치 저장
    Optional<Experience> findById(Long id);  // ID로 조회
    List<Experience> findByUserId(Long userId);  // 특정 사용자 경험치 조회
}