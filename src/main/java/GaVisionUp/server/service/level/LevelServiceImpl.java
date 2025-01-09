package GaVisionUp.server.service.level;

import GaVisionUp.server.entity.Level;
import GaVisionUp.server.entity.enums.JobGroup;
import GaVisionUp.server.repository.level.LevelRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class LevelServiceImpl implements LevelService{

    private final LevelRepository levelRepository;

    // 특정 직군(JobGroup)의 모든 레벨 가져오기
    @Override
    public List<Level> getLevelsByJobGroup(JobGroup jobGroup) {
        return levelRepository.findByJobGroup(jobGroup);
    }

    // 사용자의 총 경험치를 기준으로 현재 레벨 가져오기
    @Override
    public Level getLevelByExp(JobGroup jobGroup, int totalExp) {
        return levelRepository.findLevelByExp(jobGroup, totalExp)
                .orElseThrow(() -> new IllegalArgumentException("해당 경험치에 맞는 레벨을 찾을 수 없습니다."));
    }
}
