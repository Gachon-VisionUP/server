package GaVisionUp.server.service.exp.expbar;

import GaVisionUp.server.entity.User;
import GaVisionUp.server.entity.exp.ExpBar;
import GaVisionUp.server.repository.exp.expbar.ExpBarRepository;
import GaVisionUp.server.repository.user.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Transactional
public class ExpBarServiceImpl implements ExpBarService {
    private final ExpBarRepository expBarRepository;
    private final UserRepository userRepository;

    // ✅ 경험치 바 생성 (User와 연결)
    @Override
    public ExpBar createExpBar(ExpBar expBar) {
        if (expBar.getUser() == null || expBar.getUser().getId() == null) {
            throw new IllegalArgumentException("ExpBar에는 반드시 User 정보가 포함되어야 합니다.");
        }

        // ✅ 유저가 실제로 존재하는지 확인
        User user = userRepository.findById(expBar.getUser().getId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사원입니다."));

        expBar.setUser(user); // ✅ 올바른 User 객체 연결
        return expBarRepository.save(expBar);
    }

    // ✅ 특정 사원의 경험치 바 조회
    @Override
    public ExpBar getExpBarById(Long id) {
        return expBarRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 사원의 경험치 바가 존재하지 않습니다."));
    }

    // ✅ 특정 사원의 경험치 바 조회
    @Override
    public ExpBar getExpBarByUserId(Long userId) {
        return expBarRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("해당 사원의 경험치 바가 존재하지 않습니다."));
    }
}
