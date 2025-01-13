package GaVisionUp.server.web.controller.exp;

import GaVisionUp.server.entity.Level;
import GaVisionUp.server.entity.User;
import GaVisionUp.server.entity.enums.JobGroup;
import GaVisionUp.server.entity.exp.Experience;
import GaVisionUp.server.entity.enums.ExpType;
import GaVisionUp.server.service.exp.experience.ExperienceService;
import GaVisionUp.server.service.level.LevelService;
import GaVisionUp.server.service.user.UserQueryService;
import GaVisionUp.server.web.dto.exp.ExperienceRequest;
import GaVisionUp.server.web.dto.exp.ExperienceResponse;
import GaVisionUp.server.web.dto.exp.list.ExperienceGrowthResponse;
import GaVisionUp.server.web.dto.exp.list.ExperienceListResponse;
import GaVisionUp.server.web.dto.exp.list.ExperienceStateResponse;
import GaVisionUp.server.web.dto.level.LevelResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Year;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/experience")
@RequiredArgsConstructor
public class ExperienceController {

    private final ExperienceService experienceService;
    private final LevelService levelService;
    private final UserQueryService userQueryService;

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

    // ✅ 경험치 현황 API ("/experience/state")
    @GetMapping("/state")
    public ResponseEntity<ExperienceStateResponse> getExperienceState(
            @SessionAttribute(name = "userId", required = false) Long sessionUserId) {
        if (sessionUserId == null) {
            return ResponseEntity.badRequest().body(null);
        }

        // ✅ 사용자 정보 가져오기
        User user = userQueryService.getUserById(sessionUserId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        // ✅ 현재 레벨 정보
        Level currentLevel = user.getLevel();
        int totalExp = user.getTotalExp();

        // ✅ 다음 레벨 정보 가져오기
        Level nextLevel = levelService.getNextLevel(currentLevel.getJobGroup(), totalExp, currentLevel.getLevelName());
        int nextLevelExpRequirement = nextLevel.getMinExp();
        int nextLevelTotalExpRequirement = nextLevel.getRequiredExp();  // 다음 레벨의 총 필요 경험치

        // ✅ 올해 경험치
        int currentYear = Year.now().getValue();
        List<Experience> currentYearExperiences = experienceService.getExperiencesByCurrentYear(sessionUserId, currentYear);
        int currentYearTotalExp = currentYearExperiences.stream().mapToInt(Experience::getExp).sum();

        // ✅ 작년 경험치
        int previousYear = currentYear - 1;
        List<Experience> previousYearExperiences = experienceService.getExperiencesByPreviousYears(sessionUserId, previousYear);
        int previousYearTotalExp = previousYearExperiences.stream().mapToInt(Experience::getExp).sum();

        // ✅ 경험치 현황 응답 생성
        ExperienceStateResponse response = new ExperienceStateResponse(
                currentLevel.getLevelName(),
                totalExp,
                nextLevel.getLevelName(),
                nextLevelExpRequirement,
                nextLevelTotalExpRequirement,
                currentYearTotalExp,
                previousYearTotalExp
        );

        return ResponseEntity.ok(response);
    }

    // ✅ 전체 레벨 정보 조회 API (유저 직군 기반 필터링)
    @GetMapping("/state/all-level")
    public ResponseEntity<Map<String, List<LevelResponse>>> getAllLevels(
            @SessionAttribute(name = "userId", required = false) Long sessionUserId) {
        if (sessionUserId == null) {
            return ResponseEntity.badRequest().body(null);
        }

        // ✅ 사용자 정보 가져오기
        User user = userQueryService.getUserById(sessionUserId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        // ✅ 사용자의 직군 기반으로 레벨 목록 조회
        JobGroup jobGroup = user.getLevel().getJobGroup();
        List<Level> levels = levelService.getLevelsByJobGroup(jobGroup);

        // ✅ 응답 변환
        List<LevelResponse> levelResponses = levels.stream()
                .map(LevelResponse::new)
                .toList();

        // ✅ 특정 직군만 반환하도록 Map 형식으로 리턴
        Map<String, List<LevelResponse>> response = Map.of(jobGroup.name(), levelResponses);

        return ResponseEntity.ok(response);
    }


    // ✅ 성장 현황 API ("/experience/growth")
    @GetMapping("/growth")
    public ResponseEntity<ExperienceGrowthResponse> getExperienceGrowth(
            @SessionAttribute(name = "userId", required = false) Long sessionUserId) {
        if (sessionUserId == null) {
            return ResponseEntity.badRequest().body(null);
        }

        // ✅ 사용자 정보 가져오기
        User user = userQueryService.getUserById(sessionUserId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        // ✅ 현재 레벨 정보
        Level currentLevel = user.getLevel();
        int totalExp = user.getTotalExp();

        // ✅ 다음 레벨 정보 가져오기
        Level nextLevel = levelService.getNextLevel(currentLevel.getJobGroup(), totalExp, currentLevel.getLevelName());
        int nextLevelTotalExpRequirement = nextLevel.getRequiredExp();  // ✅ 다음 레벨의 총 필요 경험치

        // ✅ 작년까지 누적된 경험치 조회
        int currentYear = Year.now().getValue();
        int previousYear = currentYear - 1;
        List<Experience> previousYearExperiences = experienceService.getExperiencesByPreviousYears(sessionUserId, previousYear);
        int previousYearTotalExp = previousYearExperiences.stream().mapToInt(Experience::getExp).sum();

        // ✅ 올해 획득한 경험치 조회
        List<Experience> currentYearExperiences = experienceService.getExperiencesByCurrentYear(sessionUserId, currentYear);
        int currentYearTotalExp = currentYearExperiences.stream().mapToInt(Experience::getExp).sum();

        // ✅ 성장 비율 계산 (소수점 절삭)
        int previousExpPercentage = (int) ((double) previousYearTotalExp / nextLevelTotalExpRequirement * 100); // 작년까지 누적된 경험치 퍼센트
        int currentYearExpPercentage = (int) ((double) currentYearTotalExp / 9000 * 100); // 올해 경험치 퍼센트 (중위 평균 9000 기준)

        // ✅ 응답 생성
        ExperienceGrowthResponse response = new ExperienceGrowthResponse(
                nextLevel.getLevelName(),
                nextLevelTotalExpRequirement,
                previousYearTotalExp,
                previousExpPercentage,
                currentYearTotalExp,
                currentYearExpPercentage
        );

        return ResponseEntity.ok(response);
    }

}
