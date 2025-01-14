package GaVisionUp.server.web.controller.exp;

import GaVisionUp.server.entity.User;
import GaVisionUp.server.entity.enums.ExpType;
import GaVisionUp.server.entity.exp.ExpBar;
import GaVisionUp.server.entity.exp.Experience;
import GaVisionUp.server.repository.user.UserRepository;
import GaVisionUp.server.service.exp.expbar.ExpBarService;
import GaVisionUp.server.service.exp.experience.ExperienceService;
import GaVisionUp.server.service.user.UserQueryService;
import GaVisionUp.server.web.dto.exp.bar.ExpBarRequest;
import GaVisionUp.server.web.dto.exp.bar.ExpBarResponse;
import GaVisionUp.server.web.dto.exp.ring.ExpBarRingResponse;
import GaVisionUp.server.web.dto.exp.ring.ExpBarRingYearlyResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Year;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/exp-bar")
@RequiredArgsConstructor
public class ExpBarController {

    private final ExpBarService expBarService;
    private final ExperienceService experienceService;
    private final UserQueryService userQueryService;
    private final UserRepository userRepository;

    // ✅ 경험치 링 데이터 조회 API
    @GetMapping("/ring")
    public ResponseEntity<ExpBarRingResponse> getExpBarRing(
            @SessionAttribute(name = "userId", required = false) Long sessionUserId) {

        if (sessionUserId == null) {
            return ResponseEntity.badRequest().body(null);
        }

        // ✅ 사용자 정보 가져오기
        User user = userQueryService.getUserById(sessionUserId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        // ✅ ExpBar 가져오기 (없으면 생성)
        ExpBar expBar = expBarService.getOrCreateExpBarByUserId(sessionUserId);

        // ✅ 올해의 경험치
        int currentYear = Year.now().getValue();
        List<Experience> currentYearExperiences = experienceService.getExperiencesByCurrentYear(sessionUserId, currentYear);
        int currentYearTotalExp = currentYearExperiences.stream().mapToInt(Experience::getExp).sum();

        // ✅ 작년까지의 경험치
        int previousYear = currentYear - 1;
        List<Experience> previousYearExperiences = experienceService.getExperiencesByPreviousYears(sessionUserId, previousYear);
        int previousYearTotalExp = previousYearExperiences.stream().mapToInt(Experience::getExp).sum();

        // ✅ ExpBar 링 응답 생성
        ExpBarRingResponse response = new ExpBarRingResponse(
                user.getLevel().getLevelName(), // ✅ 레벨명
                user.getTotalExp(),             // ✅ 전체 경험치
                currentYearTotalExp,            // ✅ 올해 누적 경험치
                previousYearTotalExp            // ✅ 작년까지의 경험치
        );

        return ResponseEntity.ok(response);
    }

    // ✅ 경험치 링 연도별 조회 API
    @GetMapping("/ring/year")
    public ResponseEntity<ExpBarRingYearlyResponse> getExpBarYearly(
            @SessionAttribute(name = "userId", required = false) Long sessionUserId,
            @RequestParam(value = "year", required = false) Integer year) {

        if (sessionUserId == null) {
            return ResponseEntity.badRequest().body(null);
        }

        // ✅ 연도 기본값 설정 (올해)
        int targetYear = (year != null) ? year : Year.now().getValue();

        // ✅ 사용자 정보 가져오기
        User user = userQueryService.getUserById(sessionUserId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        // ✅ ExpBar 가져오기 (없으면 생성)
        ExpBar expBar = expBarService.getOrCreateExpBarByUserId(sessionUserId);

        // ✅ 연도별 경험치 내역 조회
        List<Experience> yearlyExperiences = experienceService.getExperiencesByCurrentYear(sessionUserId, targetYear);

        // ✅ 경험치 유형별 총합 계산
        Map<ExpType, Integer> experienceByType = new EnumMap<>(ExpType.class);
        for (ExpType type : ExpType.values()) {
            int total = yearlyExperiences.stream()
                    .filter(exp -> exp.getExpType() == type)
                    .mapToInt(Experience::getExp)
                    .sum();
            experienceByType.put(type, total);
        }

        // ✅ 연도별 경험치 링 응답 생성
        ExpBarRingYearlyResponse response = new ExpBarRingYearlyResponse(
                user.getLevel().getLevelName(), // ✅ 레벨명
                targetYear,                     // ✅ 조회한 연도
                experienceByType                 // ✅ 경험치 유형별 총합
        );

        return ResponseEntity.ok(response);
    }

    /*
    // ✅ 경험치 링 연도별 조회 API
    @GetMapping("/ring/year")
    public ResponseEntity<ExpBarRingYearlyResponse> getExpBarYearly(
            @SessionAttribute(name = "userId", required = false) Long sessionUserId,
            @RequestParam(value = "year", required = false) Integer year) {

        if (sessionUserId == null) {
            return ResponseEntity.badRequest().body(null);
        }

        // ✅ 연도 기본값 설정 (올해)
        int targetYear = (year != null) ? year : Year.now().getValue();

        // ✅ 사용자 정보 가져오기
        User user = userQueryService.getUserById(sessionUserId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        // ✅ ExpBar 가져오기 (없으면 생성)
        ExpBar expBar = expBarService.getOrCreateExpBarByUserId(sessionUserId);

        // ✅ 연도별 경험치 내역 조회
        List<Experience> yearlyExperiences = experienceService.getExperiencesByCurrentYear(sessionUserId, targetYear);

        // ✅ 경험치 유형별 총합 계산 (기본값 0)
        Map<ExpType, Integer> experienceByType = new EnumMap<>(ExpType.class);
        for (ExpType type : ExpType.values()) {
            experienceByType.put(type, 0); // 기본값 0으로 설정
        }

        // ✅ 실제 경험치 데이터가 있는 경우 값 업데이트
        for (Experience experience : yearlyExperiences) {
            experienceByType.put(experience.getExpType(),
                    experienceByType.get(experience.getExpType()) + experience.getExp());
        }

        // ✅ 연도별 경험치 링 응답 생성
        ExpBarRingYearlyResponse response = new ExpBarRingYearlyResponse(
                user.getLevel().getLevelName(), // ✅ 레벨명
                targetYear,                     // ✅ 조회한 연도
                experienceByType                 // ✅ 경험치 유형별 총합
        );

        return ResponseEntity.ok(response);
    }
     */


    // 최초 회원 추가 시에 경험치 바 생성
    @PostMapping("/create")
    public ResponseEntity<ExpBarResponse> createExpBar(@RequestBody ExpBarRequest request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사원입니다.")); // ✅ userId 검증

        ExpBar expBar = new ExpBar(user); // ✅ ExpBar 생성 시 유저 레벨 자동 반영
        ExpBar savedExpBar = expBarService.createExpBar(expBar);

        return ResponseEntity.ok(new ExpBarResponse(savedExpBar));  // ✅ DTO 변환 후 반환
    }



    // 특정 사원의 경험치 바 조회
    @GetMapping("/{userId}")
    public ResponseEntity<ExpBarResponse> getExpBar(@PathVariable Long userId) {
        ExpBar expBar = expBarService.getExpBarByUserId(userId);
        return ResponseEntity.ok(new ExpBarResponse(expBar));  // ✅ DTO 변환
    }
}
