package GaVisionUp.server.web.controller.quest;

import GaVisionUp.server.entity.enums.Cycle;
import GaVisionUp.server.entity.quest.job.JobQuest;
import GaVisionUp.server.service.quest.job.JobQuestService;
import GaVisionUp.server.service.quest.leader.LeaderQuestService;
import GaVisionUp.server.web.dto.quest.team.TeamJobResponse;
import GaVisionUp.server.web.dto.quest.team.leader.LeaderQuestAchievementResponse;
import GaVisionUp.server.web.dto.quest.team.leader.LeaderQuestConditionResponse;
import GaVisionUp.server.web.dto.quest.team.leader.LeaderQuestDetailResponse;
import GaVisionUp.server.web.dto.quest.team.leader.LeaderQuestListResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Year;
import java.time.YearMonth;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/team-quest")
@RequiredArgsConstructor
public class TeamQuestController {
    private final JobQuestService jobQuestService;
    private final LeaderQuestService leaderQuestService;

    // ✅ 직무별 퀘스트 조회 (연도별)
    @GetMapping("/job")
    public ResponseEntity<List<TeamJobResponse>> getJobQuests(
            @SessionAttribute(name = "userId", required = false) Long sessionUserId,
            @RequestParam(value = "year", required = false) Integer year) {

        if (sessionUserId == null) {
            return ResponseEntity.badRequest().build();
        }

        int targetYear = (year != null) ? year : Year.now().getValue();

        List<JobQuest> jobQuests = jobQuestService.getJobQuestsByYear(sessionUserId, targetYear);
        List<TeamJobResponse> response = jobQuests.stream()
                .map(TeamJobResponse::new)
                .collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }

    // ✅ 리더 부여 퀘스트 조회 (연도별, 주기별)
    @GetMapping("/leader")
    public ResponseEntity<LeaderQuestListResponse> getLeaderQuests(
            @SessionAttribute(name = "userId", required = false) Long sessionUserId,
            @RequestParam(value = "year", required = false) Integer year,
            @RequestParam(value = "cycle", required = false, defaultValue = "MONTHLY") Cycle cycle) {

        if (sessionUserId == null) {
            return ResponseEntity.badRequest().build();
        }

        int targetYear = (year != null) ? year : Year.now().getValue();
        int currentMonth = YearMonth.now().getMonthValue(); // 현재 월

        var conditions = leaderQuestService.getConditionsByUserId(sessionUserId)
                .stream()
                .map(LeaderQuestConditionResponse::new)
                .collect(Collectors.toList());

        var achievements = (cycle == Cycle.MONTHLY) ?
                leaderQuestService.getMonthlyAchievements(sessionUserId, targetYear).stream()
                        .map(LeaderQuestAchievementResponse::new)
                        .collect(Collectors.toList()) :
                leaderQuestService.getWeeklyAchievements(sessionUserId, targetYear, currentMonth).stream()
                        .map(LeaderQuestAchievementResponse::new)
                        .collect(Collectors.toList());

        return ResponseEntity.ok(new LeaderQuestListResponse(conditions, achievements));
    }

    // ✅ 리더 퀘스트 상세 조회
    @GetMapping("/leader/{id}")
    public ResponseEntity<LeaderQuestDetailResponse> getLeaderQuestDetail(@PathVariable Long id) {
        return ResponseEntity.ok(leaderQuestService.getQuestDetail(id));
    }

}
