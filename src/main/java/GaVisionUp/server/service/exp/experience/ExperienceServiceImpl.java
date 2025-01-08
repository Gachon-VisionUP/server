package GaVisionUp.server.service.exp.experience;

import GaVisionUp.server.entity.User;
import GaVisionUp.server.entity.enums.ExpType;
import GaVisionUp.server.entity.exp.Experience;
import GaVisionUp.server.repository.exp.experience.ExperienceRepository;
import GaVisionUp.server.repository.user.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class ExperienceServiceImpl implements ExperienceService {

    private final ExperienceRepository experienceRepository;
    private final UserRepository userRepository;

    // ✅ 경험치 저장 및 유저 총 경험치 반영
    @Override
    public Experience addExperience(Long userId, ExpType expType, int exp) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사원입니다."));

        Experience experience = new Experience(user, expType, exp);
        return experienceRepository.save(experience);
    }

    // ✅ 특정 경험치의 경험치 내역 조회
    @Override
    public Optional<Experience> getExperienceById(Long id) {
        return experienceRepository.findById(id)
                .or(() -> {
                    throw new IllegalArgumentException("해당 경험치 기록이 존재하지 않습니다.");
                });
    }

    // ✅ 특정 유저의 경험치 내역 조회
    @Override
    public List<Experience> getExperiencesByUserId(Long userId) {
        return experienceRepository.findByUserId(userId);
    }
}
