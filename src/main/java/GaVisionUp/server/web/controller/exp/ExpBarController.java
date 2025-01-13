package GaVisionUp.server.web.controller.exp;

import GaVisionUp.server.entity.User;
import GaVisionUp.server.entity.exp.ExpBar;
import GaVisionUp.server.entity.exp.Experience;
import GaVisionUp.server.repository.user.UserRepository;
import GaVisionUp.server.service.exp.expbar.ExpBarService;
import GaVisionUp.server.service.exp.experience.ExperienceService;
import GaVisionUp.server.service.user.UserQueryService;
import GaVisionUp.server.web.dto.exp.bar.ExpBarRequest;
import GaVisionUp.server.web.dto.exp.bar.ExpBarResponse;
import GaVisionUp.server.web.dto.exp.bar.ExpBarRingResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Year;
import java.util.List;

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
