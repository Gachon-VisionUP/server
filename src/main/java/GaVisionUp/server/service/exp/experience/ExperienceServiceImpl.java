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
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class ExperienceServiceImpl implements ExperienceService {

    private final ExperienceRepository experienceRepository;
    private final UserRepository userRepository;
    private final ExpBarRepository expBarRepository;

    // ✅ 경험치 추가 및 알림 전송
    public Experience addExperience(Long userId, ExpType expType, int exp) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사원입니다."));

        Experience experience = new Experience(user, expType, exp);
        experienceRepository.save(experience);

        return experience;
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

    // ✅ 최신 경험치 조회
    @Override
    public Optional<Experience> getLatestExperienceByUserId(Long userId) {
        return experienceRepository.findTopByUserIdOrderByObtainedDateDesc(userId);
    }

    // ✅ 특정 연도의 최신 3개 경험치 조회
    @Override
    public List<Experience> getTop3ExperiencesByYear(Long userId, int year) {
        return experienceRepository.findTop3ByUserIdAndYearOrderByObtainedDateDesc(userId, year);
    }
}
