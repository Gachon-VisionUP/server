package GaVisionUp.server.web.controller.exp;

import GaVisionUp.server.entity.exp.Experience;
import GaVisionUp.server.entity.enums.ExpType;
import GaVisionUp.server.service.exp.experience.ExperienceService;
import GaVisionUp.server.web.dto.exp.ExperienceRequest;
import GaVisionUp.server.web.dto.exp.ExperienceResponse;
import GaVisionUp.server.web.dto.exp.list.ExperienceListResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Year;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/experience")
@RequiredArgsConstructor
public class ExperienceController {

    private final ExperienceService experienceService;

    // ✅ 경험치 추가
    @PostMapping("/add")
    public ResponseEntity<ExperienceResponse> addExperience(@RequestBody ExperienceRequest request) {
        Experience experience = experienceService.addExperience(
                request.getUserId(), request.getExpType(), request.getExp()
        );
        return ResponseEntity.ok(new ExperienceResponse(experience));
    }

    // ✅ 특정 경험치 조회
    @GetMapping("/{id}")
    public ResponseEntity<ExperienceResponse> getExperienceById(@PathVariable Long id) {
        Experience experience = experienceService.getExperienceById(id)
                .orElseThrow(() -> new IllegalArgumentException("경험치 기록을 찾을 수 없습니다."));
        return ResponseEntity.ok(new ExperienceResponse(experience));
    }

    // ✅ 특정 유저의 모든 경험치 조회
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<ExperienceResponse>> getExperiencesByUserId(@PathVariable Long userId) {
        List<Experience> experiences = experienceService.getExperiencesByUserId(userId);
        List<ExperienceResponse> response = experiences.stream()
                .map(ExperienceResponse::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    // ✅ 올해(2024년) 경험치 조회
    @GetMapping("/user/{userId}/current")
    public ResponseEntity<List<ExperienceResponse>> getExperiencesByCurrentYear(@PathVariable Long userId) {
        int currentYear = Year.now().getValue();
        List<Experience> experiences = experienceService.getExperiencesByCurrentYear(userId, currentYear);
        List<ExperienceResponse> response = experiences.stream()
                .map(ExperienceResponse::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    // ✅ 작년까지(입사일부터 2023년까지) 경험치 조회
    @GetMapping("/user/{userId}/previous")
    public ResponseEntity<List<ExperienceResponse>> getExperiencesByPreviousYears(@PathVariable Long userId) {
        int previousYear = Year.now().getValue() - 1; // 작년 (ex: 2023)
        List<Experience> experiences = experienceService.getExperiencesByPreviousYears(userId, previousYear);
        List<ExperienceResponse> response = experiences.stream()
                .map(ExperienceResponse::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    // ✅ 경험치 목록 조회 (최신 1개 + 연도별 최신 3개)
    @GetMapping("/list")
    public ResponseEntity<?> getExperienceList(
            @SessionAttribute(name = "userId", required = false) Long sessionUserId,
            @RequestParam(value = "year", required = false) Integer selectedYear) {

        if (sessionUserId == null) {
            return ResponseEntity.badRequest().body("로그인이 필요합니다.");
        }

        // ✅ 기본 연도는 현재 연도
        int targetYear = (selectedYear != null) ? selectedYear : Year.now().getValue();

        // ✅ 선택한 연도의 최신 경험치 3개 조회
        List<Experience> latestThreeExperiences = experienceService.getTop3ExperiencesByYear(sessionUserId, targetYear);

        // ✅ Response 변환
        List<ExperienceResponse> top3ExperiencesResponse = latestThreeExperiences.stream()
                .map(ExperienceResponse::new)
                .collect(Collectors.toList());

        return ResponseEntity.ok(new ExperienceListResponse(targetYear, top3ExperiencesResponse));
    }

}
