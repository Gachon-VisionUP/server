package GaVisionUp.server.service.level;

import GaVisionUp.server.entity.Level;
import GaVisionUp.server.entity.enums.JobGroup;
import GaVisionUp.server.repository.level.LevelRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class LevelServiceImpl implements LevelService {

    private final LevelRepository levelRepository;

    // ✅ 특정 직군(JobGroup)의 모든 레벨 가져오기
    @Override
    public List<Level> getLevelsByJobGroup(JobGroup jobGroup) {
        return levelRepository.findByJobGroup(jobGroup);
    }

    // ✅ 사용자의 총 경험치를 기준으로 현재 레벨 가져오기
    @Override
    public Level getLevelByExp(JobGroup jobGroup, int totalExp) {
        return levelRepository.findLevelByExp(jobGroup, totalExp)
                .orElseThrow(() -> new IllegalArgumentException("해당 경험치에 맞는 레벨을 찾을 수 없습니다."));
    }

    // ✅ 현재 경험치를 기반으로 다음 레벨 가져오기
    @Override
    public Level getNextLevel(JobGroup jobGroup, int totalExp, String currentLevelName) {
        return levelRepository.findNextLevel(jobGroup, totalExp, currentLevelName)
                .orElseThrow(() -> new IllegalArgumentException("해당 직군에서 다음 레벨이 존재하지 않습니다."));
    }

    // ✅ 현재 레벨 가져오기 (레벨명 + 직군 기반)
    @Override
    public Level getLevelByNameAndJobGroup(String levelName, JobGroup jobGroup) {
        return levelRepository.findByLevelNameAndJobGroup(levelName, jobGroup)
                .orElseThrow(() -> new IllegalArgumentException("해당하는 레벨이 존재하지 않습니다."));
    }

    // ✅ 다음 레벨 찾기 (직군 + 총 경험치 기준)
    @Override
    public Optional<Level> findNextLevel(JobGroup jobGroup, int totalExp, String currentLevelName) {
        return levelRepository.findNextLevel(jobGroup, totalExp, currentLevelName);
    }
}
