package GaVisionUp.server.service.quest.leader;


import GaVisionUp.server.entity.User;
import GaVisionUp.server.entity.enums.Cycle;
import GaVisionUp.server.entity.enums.Department;
import GaVisionUp.server.entity.enums.ExpType;
import GaVisionUp.server.entity.exp.Experience;
import GaVisionUp.server.entity.quest.leader.LeaderQuest;
import GaVisionUp.server.entity.quest.leader.LeaderQuestCondition;
import GaVisionUp.server.repository.exp.experience.ExperienceRepository;
import GaVisionUp.server.repository.quest.leader.condition.LeaderQuestConditionRepository;
import GaVisionUp.server.repository.quest.leader.LeaderQuestRepository;
import GaVisionUp.server.repository.user.UserRepository;
import GaVisionUp.server.web.dto.quest.leader.LeaderQuestDetailResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class LeaderQuestServiceImpl implements LeaderQuestService {

    private final LeaderQuestRepository leaderQuestRepository;
    private final LeaderQuestConditionRepository leaderQuestConditionRepository;
    private final UserRepository userRepository;
    private final ExperienceRepository experienceRepository;

    // ✅ 특정 유저의 리더 퀘스트 목록 조회
    @Override
    public List<LeaderQuest> getLeaderQuests(Long userId) {
        return leaderQuestRepository.findByUserId(userId);
    }

    // ✅ 특정 부서, 주기별 전체 리더 퀘스트 조회
    @Override
    public List<LeaderQuest> getAllLeaderQuestsByDepartment(Department department) {
        return leaderQuestRepository.findAllByDepartmentAndCycle(department, Cycle.MONTHLY);
    }

    // ✅ 새로운 리더 퀘스트 할당 및 경험치 추가
    @Override
    public LeaderQuest assignLeaderQuest(Long userId, Cycle cycle, String questName, int month, Integer week,
                                         String achievementType, String note, LocalDate assignedDate) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("유저를 찾을 수 없습니다."));

        // ✅ 퀘스트 조건 조회
        LeaderQuestCondition condition = leaderQuestConditionRepository.findByQuestName(questName)
                .orElseThrow(() -> new IllegalArgumentException("퀘스트 조건을 찾을 수 없습니다."));

        // ✅ 주기 검증
        if (!condition.getCycle().equals(cycle)) {
            throw new IllegalArgumentException("퀘스트 주기가 조건과 일치하지 않습니다.");
        }

        // ✅ 경험치 계산
        int grantedExp = calculateGrantedExp(achievementType, condition);

        // ✅ 퀘스트 할당 (경험치 자동 반영)
        LeaderQuest assignment = LeaderQuest.create(user, cycle, questName, month, week, achievementType, grantedExp, note, assignedDate, condition);
        leaderQuestRepository.save(assignment);

        // ✅ 경험치 추가 및 레벨 반영
        addExperience(user, grantedExp);

        return assignment;
    }

    // ✅ 경험치 계산 로직 분리
    private int calculateGrantedExp(String achievementType, LeaderQuestCondition condition) {
        if ("Max".equalsIgnoreCase(achievementType)) {
            return condition.getMaxExp();
        } else if ("Median".equalsIgnoreCase(achievementType)) {
            return condition.getMedianExp();
        } else {
            throw new IllegalArgumentException("올바르지 않은 달성 유형입니다: " + achievementType);
        }
    }


    // ✅ 경험치 추가 및 레벨 업데이트
    private void addExperience(User user, int exp) {
        Experience experience = new Experience(user, ExpType.LEADER_QUEST, exp);
        experienceRepository.save(experience);
    }

    // ✅ 퀘스트 조건 목록 조회 (유저 소속 기반)
    @Override
    public List<LeaderQuestCondition> getConditionsByUserId(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        return leaderQuestConditionRepository.findByDepartment(user.getDepartment());
    }

    // ✅ 특정 연도의 퀘스트 달성 등급 조회
    @Override
    public List<LeaderQuest> getAchievementsByYear(Long userId, int year) {
        return leaderQuestRepository.findByUserIdAndYear(userId, year);
    }

    // ✅ 연도별 리더 퀘스트 조회 (월별)
    @Override
    public List<LeaderQuest> getMonthlyAchievements(Long userId, int year) {
        return leaderQuestRepository.findMonthlyByUserIdAndYear(userId, year);
    }

    // ✅ 연도 및 월별 리더 퀘스트 조회 (주별)
    @Override
    public List<LeaderQuest> getWeeklyAchievements(Long userId, int year, int month) {
        return leaderQuestRepository.findWeeklyByUserIdAndYearAndMonth(userId, year, month);
    }

    @Override
    public LeaderQuestDetailResponse getQuestDetail(Long questConditionId) {
        // ✅ 퀘스트 조건 조회
        LeaderQuestCondition condition = leaderQuestConditionRepository.findById(questConditionId)
                .orElseThrow(() -> new IllegalArgumentException("해당 퀘스트 조건을 찾을 수 없습니다."));

        // ✅ 동일한 조건을 가진 모든 퀘스트 조회
        List<LeaderQuest> quests = leaderQuestRepository.findByConditionId(questConditionId);

        return new LeaderQuestDetailResponse(condition, quests);
    }

}
