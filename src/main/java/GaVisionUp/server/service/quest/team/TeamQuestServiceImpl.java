package GaVisionUp.server.service.quest.team;

import GaVisionUp.server.entity.User;
import GaVisionUp.server.entity.enums.Cycle;
import GaVisionUp.server.entity.enums.TeamQuestGrade;
import GaVisionUp.server.entity.quest.TeamQuest;
import GaVisionUp.server.entity.quest.job.JobQuest;
import GaVisionUp.server.entity.quest.leader.LeaderQuest;
import GaVisionUp.server.repository.quest.team.TeamQuestRepository;
import GaVisionUp.server.repository.quest.job.JobQuestRepository;
import GaVisionUp.server.repository.quest.leader.LeaderQuestRepository;
import GaVisionUp.server.repository.user.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class TeamQuestServiceImpl implements TeamQuestService {

    private final TeamQuestRepository teamQuestRepository;
    private final JobQuestRepository jobQuestRepository;
    private final LeaderQuestRepository leaderQuestRepository;
    private final UserRepository userRepository;

    @Override
    public List<TeamQuest> getUserMonthlyQuests(Long userId, int year, int month) {
        return teamQuestRepository.findByUserAndMonth(userId, year, month);
    }

    @Override
    public void recordTeamQuest(Long userId, int year, int month, LocalDate recordedDate) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("유저를 찾을 수 없습니다."));

        int day = recordedDate.getDayOfMonth();
        int week = calculateWeekOfMonth(recordedDate);
        DayOfWeek dayOfWeek = recordedDate.getDayOfWeek();
        int round = calculateRound(month, week);

        // ✅ 정확한 월 & 주차 기준 평가 데이터 가져오기
        Optional<JobQuest> jobQuestWeeklyOpt = jobQuestRepository.findByDepartmentAndCycleAndRound(
                user.getDepartment().name(), user.getPart(), Cycle.WEEKLY, round
        );

        Optional<JobQuest> jobQuestMonthlyOpt = jobQuestRepository.findByDepartmentAndCycleAndRound(
                user.getDepartment().name(), user.getPart(), Cycle.MONTHLY, month
        );

        Optional<LeaderQuest> leaderQuestWeeklyOpt = leaderQuestRepository.findByDepartmentAndCycleAndRound(
                user.getDepartment().name(), Cycle.WEEKLY, round
        );

        Optional<LeaderQuest> leaderQuestMonthlyOpt = leaderQuestRepository.findByDepartmentAndCycleAndMonth(
                user.getDepartment().name(), Cycle.MONTHLY, month
        );

        // ✅ 정확한 등급 할당 (null 체크)
        TeamQuestGrade jobGrade = jobQuestWeeklyOpt.map(JobQuest::getQuestGrade).orElse(null);
        TeamQuestGrade jobGradeMonthly = jobQuestMonthlyOpt.map(JobQuest::getQuestGrade).orElse(null);
        TeamQuestGrade leaderGrade = leaderQuestWeeklyOpt.map(q -> TeamQuestGrade.valueOf(q.getAchievementType())).orElse(null);
        TeamQuestGrade leaderGradeMonthly = leaderQuestMonthlyOpt.map(q -> TeamQuestGrade.valueOf(q.getAchievementType())).orElse(null);

        log.info("✅ [INFO] {}월 {}주차 직무 평가 결과: {} (주간) / {} (월간)", month, week, jobGrade, jobGradeMonthly);
        log.info("✅ [INFO] {}월 {}주차 리더 평가 결과: {} (주간) / {} (월간)", month, week, leaderGrade, leaderGradeMonthly);

        // ✅ 월간 데이터가 존재하면 적용, 없으면 주간 데이터 적용
        TeamQuest quest = TeamQuest.create(user, month, week, day, dayOfWeek, recordedDate,
                jobGrade != null ? jobGrade : jobGradeMonthly,
                leaderGrade != null ? leaderGrade : leaderGradeMonthly);
        teamQuestRepository.save(quest);

        log.info("📌 [DEBUG] TeamQuest 저장 완료 - {} {}월 {}주차 {}일({}): 직무: {}, 리더: {}",
                user.getName(), month, week, day, dayOfWeek,
                jobGrade != null ? jobGrade : jobGradeMonthly,
                leaderGrade != null ? leaderGrade : leaderGradeMonthly);
    }


    private int calculateWeekOfMonth(LocalDate date) {
        LocalDate firstDayOfMonth = date.withDayOfMonth(1);
        int firstDayWeekValue = firstDayOfMonth.getDayOfWeek().getValue();
        int dayOfMonth = date.getDayOfMonth();
        int week = ((dayOfMonth + firstDayWeekValue - 2) / 7) + 1;

        if (firstDayWeekValue == 7 && date.getDayOfWeek() == DayOfWeek.SUNDAY) {
            week = 1;
        }
        return week;
    }

    private int calculateRound(int month, int week) {
        int[] MONTHLY_WEEKS = {5, 4, 4, 4, 5, 4, 5, 4, 4, 5, 4, 5};
        int round = week;
        for (int i = 0; i < month - 1; i++) {
            round += MONTHLY_WEEKS[i];
        }
        return round;
    }
}
