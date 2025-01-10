package GaVisionUp.server.web.controller.quest.leader;

import GaVisionUp.server.entity.enums.Department;
import GaVisionUp.server.entity.quest.leader.LeaderQuest;
import GaVisionUp.server.service.quest.leader.LeaderQuestService;
import GaVisionUp.server.web.dto.quest.leader.LeaderQuestRequest;
import GaVisionUp.server.web.dto.quest.leader.LeaderQuestResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/leader-quest")
@RequiredArgsConstructor
public class LeaderQuestController {

    private final LeaderQuestService leaderQuestService;

    // ✅ 특정 유저의 리더 퀘스트 목록 조회
    @GetMapping("/{userId}")
    public ResponseEntity<List<LeaderQuestResponse>> getLeaderQuests(@PathVariable Long userId) {
        List<LeaderQuestResponse> quests = leaderQuestService.getLeaderQuests(userId)
                .stream().map(LeaderQuestResponse::new)
                .collect(Collectors.toList());

        return ResponseEntity.ok(quests);
    }

    // ✅ 특정 부서의 전체 리더 퀘스트 조회
    @GetMapping("/department/{department}")
    public ResponseEntity<List<LeaderQuestResponse>> getAllLeaderQuests(@PathVariable String department) {
        List<LeaderQuestResponse> quests = leaderQuestService.getAllLeaderQuestsByDepartment(Department.valueOf(department))
                .stream().map(LeaderQuestResponse::new)
                .collect(Collectors.toList());

        return ResponseEntity.ok(quests);
    }

    // ✅ 새로운 리더 퀘스트 할당 (경험치 자동 부여)
    @PostMapping("/assign")
    public ResponseEntity<LeaderQuestResponse> assignLeaderQuest(@RequestBody LeaderQuestRequest request) {
        LeaderQuest quest = leaderQuestService.assignLeaderQuest(
                request.getUserId(),
                request.getCycle(),
                request.getQuestName(),
                request.getMonth(),
                request.getWeek(),
                request.getAchievementType(),
                request.getNote(),
                request.getAssignedDate()
        );

        return ResponseEntity.ok(new LeaderQuestResponse(quest)); // ✅ DTO 반환
    }
}
