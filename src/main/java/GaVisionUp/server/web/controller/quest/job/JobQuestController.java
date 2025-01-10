package GaVisionUp.server.web.controller.quest.job;

import GaVisionUp.server.service.quest.job.JobQuestService;
import GaVisionUp.server.web.dto.quest.job.JobQuestRequest;
import GaVisionUp.server.web.dto.quest.job.JobQuestResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/job-quest")
@RequiredArgsConstructor
public class JobQuestController {

    private final JobQuestService jobQuestService;

    // ✅ 직무별 퀘스트 경험치 평가 API
    @PostMapping("/evaluate")
    public ResponseEntity<Void> evaluateJobQuest(@RequestBody JobQuestRequest request) {
        jobQuestService.evaluateJobQuest(
                request.getDepartment(), request.getPart(), request.getCycle(), request.getRound());

        return ResponseEntity.status(HttpStatus.CREATED).build(); // ✅ 201 Created 반환
    }

    // ✅ 특정 부서, 직무 그룹, 주기의 JobQuest 목록 조회
    @GetMapping("/{department}/{part}/{cycle}")
    public ResponseEntity<List<JobQuestResponse>> getAllJobQuests(
            @PathVariable String department,
            @PathVariable int part,
            @PathVariable String cycle) {
        List<JobQuestResponse> jobQuests = jobQuestService.getAllJobQuests(department, part, cycle)
                .stream().map(JobQuestResponse::new).toList();
        return ResponseEntity.ok(jobQuests);
    }
}