package GaVisionUp.server.web.controller.jobquest;

import GaVisionUp.server.service.jobquest.JobQuestService;
import GaVisionUp.server.web.dto.quest.JobQuestRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/job-quest")
@RequiredArgsConstructor
public class JobQuestController {

    private final JobQuestService jobQuestService;

    // ✅ 직무별 퀘스트 경험치 평가 API
    @PostMapping("/evaluate")
    public ResponseEntity<Void> evaluateJobQuest(@RequestBody JobQuestRequest request) {
        jobQuestService.evaluateJobQuest(
                request.getDepartment(), request.getPart(), request.getCycle(), request.getRound(), request.getProductivity());

        return ResponseEntity.status(HttpStatus.CREATED).build(); // ✅ 201 Created 반환
    }

}
