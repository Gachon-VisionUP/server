package GaVisionUp.server.service.quest.team;

import GaVisionUp.server.entity.User;
import GaVisionUp.server.entity.enums.TeamQuestGrade;
import GaVisionUp.server.entity.quest.TeamQuest;
import GaVisionUp.server.entity.quest.job.JobQuest;
import GaVisionUp.server.repository.quest.team.TeamQuestRepository;
import GaVisionUp.server.repository.quest.job.JobQuestRepository;
import GaVisionUp.server.repository.user.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class TeamQuestServiceImpl implements TeamQuestService {

    private final TeamQuestRepository teamQuestRepository;
    private final JobQuestRepository jobQuestRepository;
    private final UserRepository userRepository;

    // ✅ 특정 유저의 월별 팀 퀘스트 기록 조회
    @Override
    public List<TeamQuest> getUserMonthlyQuests(Long userId, int year, int month) {
        return teamQuestRepository.findByUserAndMonth(userId, year, month);
    }

    // ✅ 특정 유저의 팀 퀘스트 기록 추가 (JobQuest 평가 등급 반영)
    @Override
    public void recordTeamQuest(Long userId, int year, int month, LocalDate recordedDate) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("유저를 찾을 수 없습니다."));

        int week = getWeekOfMonth(recordedDate);
        int day = recordedDate.getDayOfMonth();

        // ✅ JobQuest 평가 기록 조회 (month & week 기반)
        Optional<JobQuest> jobQuestOpt = jobQuestRepository.findByDepartmentAndMonthAndWeek(
                user.getDepartment().name(), user.getPart(), month, week
        );

        // ✅ 평가 기록이 존재하면 등급 반영, 없으면 기본값 MIN
        TeamQuestGrade questGrade = jobQuestOpt.map(JobQuest::getQuestGrade).orElse(TeamQuestGrade.MIN);

        if (jobQuestOpt.isPresent()) {
            log.info("✅ [INFO] {}월 {}주차 평가 결과 적용: {} ({}점)", month, week, questGrade, jobQuestOpt.get().getGrantedExp());
        } else {
            log.warn("⚠️ [WARN] {}월 {}주차 평가 기록 없음 - 기본값 MIN으로 설정", month, week);
        }

        // ✅ TeamQuest 생성 및 저장
        TeamQuest quest = TeamQuest.create(user, month, week, day, recordedDate, questGrade);
        teamQuestRepository.save(quest);

        log.info("📌 [DEBUG] TeamQuest 저장 완료 - {} {}월 {}주차 {}일: {}", user.getName(), month, week, day, questGrade);
    }

    // ✅ 주차 계산 (해당 월의 몇 번째 주인지 확인)
    private int getWeekOfMonth(LocalDate date) {
        LocalDate firstDayOfMonth = date.with(TemporalAdjusters.firstDayOfMonth());
        return (date.getDayOfMonth() - 1) / 7 + 1; // ✅ 1~7일 → 1주, 8~14일 → 2주, ...
    }
}
