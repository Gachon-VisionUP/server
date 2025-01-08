package GaVisionUp.server.repository.exp.expbar;

import GaVisionUp.server.entity.exp.ExpBar;

import java.util.Optional;

public interface ExpBarRepository {
    ExpBar save(ExpBar expBar); // 저장 (INSERT/UPDATE)
    Optional<ExpBar> findById(Long id);  // 아이디로 조회
    Optional<ExpBar> findByUserId(Long userId); // 사번으로 조회
}