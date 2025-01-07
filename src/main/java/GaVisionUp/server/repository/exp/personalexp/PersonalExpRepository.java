package GaVisionUp.server.repository.exp.personalexp;

import GaVisionUp.server.entity.exp.PersonalExp;

import java.util.List;
import java.util.Optional;

public interface PersonalExpRepository {
    PersonalExp save(PersonalExp personalExp);  // 개인 경험치 저장
    Optional<PersonalExp> findById(Long id);  // ID로 조회
    List<PersonalExp> findByUserId(Long userId);  // 특정 사용자 경험치 조회
}