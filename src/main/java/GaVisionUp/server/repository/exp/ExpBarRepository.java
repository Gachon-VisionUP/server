package GaVisionUp.server.repository.exp;

import GaVisionUp.server.entity.exp.ExpBar;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ExpBarRepository {
    ExpBar save(ExpBar expBar); // 저장 (INSERT/UPDATE)
    Optional<ExpBar> findById(Long id); // ID로 조회
    Optional<ExpBar> findByUserId(int userId); // 사번으로 조회
    void updateTotalExp(int userId, int exp); // 경험치 업데이트
}