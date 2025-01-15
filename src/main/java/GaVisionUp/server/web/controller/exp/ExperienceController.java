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
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/experience")
@RequiredArgsConstructor
public class ExperienceController {

    private final ExperienceService experienceService;
    private final LevelService levelService;
    private final UserQueryService userQueryService;

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

        // ✅ 현재 레벨 정보 가져오기 (객체 전체 조회)
        Level currentLevel = levelService.getLevelByNameAndJobGroup(user.getLevel().getLevelName(), user.getLevel().getJobGroup());
        int totalExp = user.getTotalExp();

        // ✅ 다음 레벨 정보 가져오기
        Optional<Level> nextLevelOpt = levelService.findNextLevel(currentLevel.getJobGroup(), totalExp, currentLevel.getLevelName());
        if (nextLevelOpt.isEmpty()) {
            return ResponseEntity.badRequest().body(null);  // 다음 레벨이 존재하지 않을 경우
        }
        Level nextLevel = nextLevelOpt.get();
        int nextLevelExpRequirement = nextLevel.getMinExp() - user.getTotalExp();
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

        // ✅ 현재 레벨 정보 가져오기 (레벨명 + 직군 기반 조회)
        Level currentLevel = levelService.getLevelByNameAndJobGroup(user.getLevel().getLevelName(), user.getLevel().getJobGroup());
        int totalExp = user.getTotalExp();

        // ✅ 다음 레벨 정보 가져오기
        Optional<Level> nextLevelOpt = levelService.findNextLevel(currentLevel.getJobGroup(), totalExp, currentLevel.getLevelName());
        if (nextLevelOpt.isEmpty()) {
            return ResponseEntity.badRequest().body(null);  // 다음 레벨이 없을 경우
        }
        Level nextLevel = nextLevelOpt.get();
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
