package GaVisionUp.server.web.controller.quest;

import GaVisionUp.server.service.quest.team.TeamQuestService;
import GaVisionUp.server.web.dto.quest.team.TeamQuestRequest;
import GaVisionUp.server.web.dto.quest.team.TeamQuestResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/team-quest")
@RequiredArgsConstructor
public class TeamQuestController {

    private final TeamQuestService teamQuestService;

    // ✅ 팀 퀘스트 기록 추가 API
    @PostMapping("/record")
    public ResponseEntity<Void> recordTeamQuest(@RequestBody TeamQuestRequest request) {
        teamQuestService.recordTeamQuest(
                request.getUserId(),
                request.getYear(),
                request.getMonth(),
                request.getRecordedDate()
        );

        return ResponseEntity.status(HttpStatus.CREATED).build(); // ✅ 201 Created 반환
    }

    // ✅ 특정 유저의 월별 팀 퀘스트 기록 조회
    @GetMapping("/{userId}/{year}/{month}")
    public ResponseEntity<List<TeamQuestResponse>> getUserMonthlyQuests(
            @PathVariable Long userId,
            @PathVariable int year,
            @PathVariable int month) {
        List<TeamQuestResponse> teamQuests = teamQuestService.getUserMonthlyQuests(userId, year, month)
                .stream().map(TeamQuestResponse::new).toList();
        return ResponseEntity.ok(teamQuests);
    }
}
