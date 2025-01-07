package GaVisionUp.server.service.exp;


import GaVisionUp.server.entity.exp.ExpBar;
import GaVisionUp.server.repository.exp.ExpBarRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ExpBarServiceImpl implements ExpBarService {
    private final ExpBarRepository expBarRepository;

    // 경험치 바 생성
    @Override
    public ExpBar createExpBar(ExpBar expBar) {
        return expBarRepository.save(expBar);
    }

    // 특정 사원의 경험치 바 조회
    @Override
    public ExpBar getExpBarByUserId(int userId) {
        return expBarRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("해당 사원의 경험치 바가 존재하지 않습니다."));
    }

    // 경험치 추가
    @Override
    @Transactional
    public ExpBar addExperience(int userId, int experience) {
        expBarRepository.updateTotalExp(userId, experience);
        return getExpBarByUserId(userId);
    }
}