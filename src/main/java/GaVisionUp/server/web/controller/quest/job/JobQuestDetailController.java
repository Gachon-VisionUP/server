package GaVisionUp.server.web.controller.quest.job;

import GaVisionUp.server.entity.enums.Cycle;
import GaVisionUp.server.service.quest.job.detail.JobQuestDetailService;
import GaVisionUp.server.web.dto.quest.job.detail.JobQuestDetailRequest;
import GaVisionUp.server.web.dto.quest.job.detail.JobQuestDetailResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/job-quest/detail")
@RequiredArgsConstructor
public class JobQuestDetailController {

    private final JobQuestDetailService jobQuestDetailService;

    // ✅ JobQuestDetail 추가 API
    @PostMapping("/add")
    public ResponseEntity<Void> addJobQuestDetail(@RequestBody JobQuestDetailRequest request) {
        jobQuestDetailService.saveJobQuestDetail(
                request.getDepartment(), request.getPart(), request.getCycle(),
                request.getRound(), request.getSales(), request.getLaborCost(), request.getRecordedDate());

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    // ✅ 특정 부서, 직무 그룹, 주기의 JobQuestDetail 목록 조회
    @GetMapping("/{department}/{part}/{cycle}")
    public ResponseEntity<List<JobQuestDetailResponse>> getAllJobQuestDetails(
            @PathVariable String department,
            @PathVariable int part,
            @PathVariable Cycle cycle) {
        List<JobQuestDetailResponse> details = jobQuestDetailService.getAllJobQuestDetails(department, part, cycle)
                .stream().map(JobQuestDetailResponse::new).toList();
        return ResponseEntity.ok(details);
    }
}