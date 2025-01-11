package GaVisionUp.server.service.quest.team;

import GaVisionUp.server.entity.User;
import GaVisionUp.server.entity.enums.Cycle;
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

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.WeekFields;
import java.util.List;
import java.util.Locale;
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

        int day = recordedDate.getDayOfMonth(); // ✅ day 값 추출
        int week = calculateWeekOfMonth(recordedDate); // ✅ 자동 주차 계산
        DayOfWeek dayOfWeek = recordedDate.getDayOfWeek(); // ✅ 요일 정보 추출

        int round = calculateRound(Cycle.WEEKLY, month, week); // ✅ JobQuest와 매칭되는 round 계산

        // ✅ JobQuest 평가 기록 조회 (round 기준)
        Optional<JobQuest> jobQuestOpt = jobQuestRepository.findByDepartmentAndRound(
                user.getDepartment().name(), user.getPart(), "WEEKLY", round
        );

        TeamQuestGrade questGrade = jobQuestOpt.map(JobQuest::getQuestGrade).orElse(TeamQuestGrade.MIN);

        if (jobQuestOpt.isPresent()) {
            log.info("✅ [INFO] {}월 {}주차 평가 결과 적용: {} ({}점)", month, week, questGrade, jobQuestOpt.get().getGrantedExp());
        } else {
            log.warn("⚠️ [WARN] {}월 {}주차 평가 기록 없음 - 기본값 MIN으로 설정", month, week);
        }

        // ✅ month, week, day, dayOfWeek 저장
        TeamQuest quest = TeamQuest.create(user, month, week, day, dayOfWeek, recordedDate, questGrade);
        teamQuestRepository.save(quest);

        log.info("📌 [DEBUG] TeamQuest 저장 완료 - {} {}월 {}주차 {}일({}): {}", user.getName(), month, week, day, dayOfWeek, questGrade);
    }

    // ✅ 월별 주차 개수 (실제 주 개수를 기반으로 round 계산)
    private static final int[] MONTHLY_WEEKS = {5, 4, 4, 4, 5, 4, 5, 4, 4, 5, 4, 5};

    // ✅ CYCLE에 따라 round 계산 (월은 1~12, 주는 1~52)
    private int calculateRound(Cycle cycle, int month, int week) {
        if (cycle == Cycle.MONTHLY) {
            return month; // ✅ 월 단위 퀘스트: round = month (1~12)
        } else if (cycle == Cycle.WEEKLY) {
            int round = week; // ✅ 기본적으로 주차 값 (1~5)

            // ✅ 해당 월 이전까지의 주 수를 누적하여 round 계산
            for (int i = 0; i < month - 1; i++) {
                round += MONTHLY_WEEKS[i];
            }
            return round; // ✅ 최종 계산된 round 값 반환 (1~52)
        }
        throw new IllegalArgumentException("잘못된 Cycle 값입니다: " + cycle);
    }

    // ✅ 주어진 날짜 기준으로 몇 번째 주인지 계산
    private int calculateWeekOfMonth(LocalDate date) {
        // 1일이 속한 주의 첫 번째 날을 찾음 (월요일 시작 기준)
        LocalDate firstDayOfMonth = date.withDayOfMonth(1);
        int firstDayWeekValue = firstDayOfMonth.getDayOfWeek().getValue(); // 1: 월요일 ~ 7: 일요일

        int dayOfMonth = date.getDayOfMonth();
        int week = ((dayOfMonth + firstDayWeekValue - 2) / 7) + 1; // ✅ (1~7: 1주차, 8~14: 2주차 ...)

        // 첫 번째 주에 일요일(7)이 포함되면, 그 주는 무조건 1주차로 설정
        if (firstDayWeekValue == 7 && date.getDayOfWeek() == DayOfWeek.SUNDAY) {
            week = 1;
        }

        return week;
    }

}
