package GaVisionUp.server.web.controller.quest;

import GaVisionUp.server.entity.enums.Cycle;
import GaVisionUp.server.entity.quest.job.JobQuest;
import GaVisionUp.server.service.quest.job.JobQuestService;
import GaVisionUp.server.service.quest.leader.LeaderQuestService;
import GaVisionUp.server.web.dto.quest.team.leader.LeaderQuestAchievementResponse;
import GaVisionUp.server.web.dto.quest.team.leader.LeaderQuestConditionResponse;
import GaVisionUp.server.web.dto.quest.team.leader.detail.LeaderQuestDetailResponse;
import GaVisionUp.server.web.dto.quest.team.TeamJobResponse;
import GaVisionUp.server.web.dto.quest.team.leader.LeaderQuestListResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Year;
import java.time.YearMonth;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/team-quest")
@RequiredArgsConstructor
public class TeamQuestController {
    private final JobQuestService jobQuestService;
    private final LeaderQuestService leaderQuestService;

    // ✅ 직무별 퀘스트 조회 (연도별)
    @GetMapping("/job")
    @Operation(summary = "직무별 퀘스트 조회 API", description = "직무별 퀘스트를 조회합니다.(연도별 조회)")
    @Parameters({
            @Parameter(name = "year", description = "조회할 연도")
    })
    public ResponseEntity<List<TeamJobResponse>> getJobQuests(
            @Parameter(hidden = true) @SessionAttribute(name = "userId", required = false) Long sessionUserId,
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

    // ✅ 리더 부여 퀘스트 조회 (연도별)
    @GetMapping("/leader")
    @Operation(summary = "리더 부여 퀘스트 조회 API", description = "리더 부여 퀘스트를 조회합니다.(연도별 조회)")
    @Parameters({
            @Parameter(name = "year", description = "조회할 연도")
    })
    public ResponseEntity<LeaderQuestListResponse> getLeaderQuests(
            @Parameter(hidden = true) @SessionAttribute(name = "userId", required = false) Long sessionUserId,
            @RequestParam(value = "year", required = false) Integer year) {

        if (sessionUserId == null) {
            return ResponseEntity.badRequest().build();
        }

        int targetYear = (year != null) ? year : Year.now().getValue();

        // ✅ 상단 퀘스트 목록 (조건) 조회 → 항상 유저의 소속을 기준으로 모든 퀘스트 반환
        var conditions = leaderQuestService.getConditionsByUserId(sessionUserId)
                .stream()
                .map(LeaderQuestConditionResponse::new)
                .collect(Collectors.toList());

        // ✅ 퀘스트 달성 데이터 조회 (월별 + 주별 모두 포함)
        List<LeaderQuestAchievementResponse> allAchievements = leaderQuestService.getAllAchievements(sessionUserId, targetYear)
                .stream()
                .map(LeaderQuestAchievementResponse::new)
                .collect(Collectors.toList());

        // ✅ 퀘스트 **명**을 기준으로 데이터 그룹화
        Map<String, List<LeaderQuestAchievementResponse>> groupedAchievements = allAchievements.stream()
                .collect(Collectors.groupingBy(LeaderQuestAchievementResponse::getQuestName));

        // ✅ 응답 객체 생성
        return ResponseEntity.ok(new LeaderQuestListResponse(conditions, groupedAchievements));
    }



    // ✅ 로그인한 유저의 해당 퀘스트 달성 내용만 조회
    @GetMapping("/leader/{id}")
    @Operation(summary = "리더 부여 퀘스트 상세 조회 API", description = "로그인한 유저의 해당 퀘스트 달성 내용만 조회합니다.")
    @Parameters({
            @Parameter(name = "id", description = "퀘스트 id")
    })
    public ResponseEntity<LeaderQuestDetailResponse> getLeaderQuestDetail(
            @Parameter(hidden = true) @SessionAttribute(name = "userId", required = false) Long userId,
            @PathVariable Long id) {

        if (userId == null) {
            return ResponseEntity.badRequest().build();
        }

        // ✅ 로그인한 유저가 해당 퀘스트를 달성한 경우만 조회
        LeaderQuestDetailResponse detail = leaderQuestService.getQuestDetailByUserId(userId, id);

        return ResponseEntity.ok(detail);
    }

}
