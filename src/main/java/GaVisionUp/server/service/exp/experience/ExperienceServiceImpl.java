package GaVisionUp.server.service.exp.experience;

import GaVisionUp.server.entity.User;
import GaVisionUp.server.entity.enums.ExpType;
import GaVisionUp.server.entity.exp.ExpBar;
import GaVisionUp.server.entity.exp.Experience;
import GaVisionUp.server.repository.exp.expbar.ExpBarRepository;
import GaVisionUp.server.repository.exp.experience.ExperienceRepository;
import GaVisionUp.server.repository.user.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Year;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class ExperienceServiceImpl implements ExperienceService {

    private final ExperienceRepository experienceRepository;
    private final UserRepository userRepository;
    private final ExpBarRepository expBarRepository;

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

    // ✅ 올해(Current Year) 경험치 조회
    @Override
    public List<Experience> getExperiencesByCurrentYear(Long userId, int currentYear) {
        ExpBar expBar = expBarRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("ExpBar를 찾을 수 없습니다."));
        return experienceRepository.findByUserIdAndCurrentYear(userId, currentYear, expBar);
    }

    // ✅ 특정 유저의 작년까지 경험치 조회
    @Override
    public List<Experience> getExperiencesByPreviousYears(Long userId, int previousYear) {
        ExpBar expBar = expBarRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("ExpBar를 찾을 수 없습니다."));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사원입니다."));
        return experienceRepository.findByUserIdAndPreviousYears(userId, previousYear, user.getJoinDate(), expBar);
    }
}
