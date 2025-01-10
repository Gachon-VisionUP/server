package GaVisionUp.server.web.controller.quest.leader;

import GaVisionUp.server.entity.enums.Department;
import GaVisionUp.server.entity.quest.leader.LeaderQuestCondition;
import GaVisionUp.server.service.quest.leader.condition.LeaderQuestConditionService;
import GaVisionUp.server.web.dto.quest.leader.condition.LeaderQuestConditionRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/leader-quest/condition")
@RequiredArgsConstructor
public class LeaderQuestConditionController {

    private final LeaderQuestConditionService leaderQuestConditionService;

    // ✅ 특정 부서의 퀘스트 조건 목록 조회
    @GetMapping("/{department}")
    public ResponseEntity<List<LeaderQuestCondition>> getConditionsByDepartment(@PathVariable String department) {
        return ResponseEntity.ok(leaderQuestConditionService.getConditionsByDepartment(Department.valueOf(department)));
    }

    // ✅ 특정 퀘스트명으로 조회
    @GetMapping("/quest/{questName}")
    public ResponseEntity<LeaderQuestCondition> getConditionByQuestName(@PathVariable String questName) {
        return ResponseEntity.ok(leaderQuestConditionService.getConditionByQuestName(questName));
    }

    // ✅ 새로운 퀘스트 조건 저장
    @PostMapping("/create")
    public ResponseEntity<LeaderQuestCondition> createQuestCondition(@RequestBody LeaderQuestConditionRequest request) {
        LeaderQuestCondition condition = leaderQuestConditionService.saveQuestCondition(
                request.getDepartment(),
                request.getCycle(),
                request.getQuestName(),
                request.getWeight(),
                request.getMaxCondition(),
                request.getMedianCondition(),
                request.getDescription()
        );
        return ResponseEntity.ok(condition);
    }
}